package com.kronifyapp.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kronifyapp.data.local.NotificationStore
import com.kronifyapp.data.local.SessionStore
import com.kronifyapp.domain.model.BusinessAppointment
import com.kronifyapp.domain.usecase.GetBusinessAgendaUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AppointmentNotificationWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            NotificationWorkerEntryPoint::class.java,
        )
        val store = entryPoint.notificationStore()
        if (!store.enabled) return Result.success()

        val token = entryPoint.sessionStore().token ?: return Result.success()
        if (!KronifyNotifications.canPost(applicationContext)) return Result.success()

        val today = LocalDate.now()
        val agenda = runCatching {
            entryPoint.getBusinessAgendaUseCase()(
                token,
                today.minusDays(1).toString(),
                today.plusDays(2).toString(),
            )
        }.getOrNull() ?: return Result.success()

        val previous = store.knownAppointments()
        val current = agenda.associateBy { it.appointmentId }
        val fingerprints = current.mapValues { (_, appointment) -> appointment.fingerprint() }

        // Primera ejecución: guarda el estado actual. Si hay una cita recién
        // creada, la notificamos para no perder el primer aviso.
        if (previous.isEmpty()) {
            val now = System.currentTimeMillis()
            current.values
                .filter { it.status != "CANCELLED" && isRecent(it.createdAt) }
                .forEach { notifyNew(it) }
            store.saveKnownAppointments(fingerprints)
            store.lastCheckAt = now
            return Result.success()
        }

        current.values.forEach { appointment ->
            val oldFingerprint = previous[appointment.appointmentId]
            if (oldFingerprint == null) {
                if (appointment.status != "CANCELLED") {
                    notifyNew(appointment)
                }
            } else if (oldFingerprint != fingerprints[appointment.appointmentId]) {
                notifyChange(appointment, oldFingerprint)
            }
        }

        val now = LocalDateTime.now()
        val reminded = store.remindedAppointments()
        current.values
            .filter { it.status == "CONFIRMED" }
            .filter { it.appointmentId !in reminded }
            .filter {
                val start = parseDate(it.startAt) ?: return@filter false
                start.isAfter(now) && start.isBefore(now.plusHours(24))
            }
            .forEach { appointment ->
                notifyReminder(appointment)
                store.markReminded(appointment.appointmentId)
            }

        store.saveKnownAppointments(fingerprints)
        store.lastCheckAt = System.currentTimeMillis()
        return Result.success()
    }

    private fun notifyNew(appointment: BusinessAppointment) {
        KronifyNotifications.show(
            applicationContext,
            notificationId(appointment.appointmentId, 1),
            "Nueva cita agendada",
            "${appointment.customerName} · ${appointment.serviceName} · ${formatDate(appointment.startAt)}",
        )
    }

    private fun notifyChange(appointment: BusinessAppointment, oldFingerprint: String) {
        val oldStatus = oldFingerprint.substringBefore('|')
        val oldStart = oldFingerprint.substringAfter('|', "")
        when {
            appointment.status == "CANCELLED" -> KronifyNotifications.show(
                applicationContext,
                notificationId(appointment.appointmentId, 2),
                "Cita cancelada",
                "${appointment.customerName} · ${appointment.serviceName}",
            )

            oldStart != appointment.startAt -> KronifyNotifications.show(
                applicationContext,
                notificationId(appointment.appointmentId, 3),
                "Cita reprogramada",
                "${appointment.customerName} · nueva hora: ${formatDate(appointment.startAt)}",
            )

            appointment.status == "CONFIRMED" && oldStatus != "CONFIRMED" -> KronifyNotifications.show(
                applicationContext,
                notificationId(appointment.appointmentId, 4),
                "Cita confirmada",
                "${appointment.customerName} · ${appointment.serviceName}",
            )
        }
    }

    private fun notifyReminder(appointment: BusinessAppointment) {
        KronifyNotifications.show(
            applicationContext,
            notificationId(appointment.appointmentId, 5),
            "Recordatorio de cita",
            "${appointment.customerName} · ${appointment.serviceName} · ${formatDate(appointment.startAt)}",
        )
    }

    private fun notificationId(appointmentId: Long, type: Int): Int =
        ((appointmentId.hashCode() * 31) + type) and 0x7FFFFFFF

    private fun BusinessAppointment.fingerprint(): String = "$status|$startAt"

    private fun parseDate(value: String): LocalDateTime? =
        runCatching { LocalDateTime.parse(value) }.getOrNull()

    private fun isRecent(value: String): Boolean {
        val created = parseDate(value) ?: return false
        val now = LocalDateTime.now(java.time.ZoneId.of("America/Bogota"))
        return created.isAfter(now.minusHours(24))
    }

    private fun formatDate(value: String): String {
        val parsed = parseDate(value) ?: return value
        return parsed.format(DateTimeFormatter.ofPattern("dd MMM, HH:mm", Locale.forLanguageTag("es-CO")))
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NotificationWorkerEntryPoint {
    fun sessionStore(): SessionStore
    fun notificationStore(): NotificationStore
    fun getBusinessAgendaUseCase(): GetBusinessAgendaUseCase
}
