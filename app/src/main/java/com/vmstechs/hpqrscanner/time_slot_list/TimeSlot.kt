package com.vmstechs.hpqrscanner.time_slot_list

import java.io.Serializable

data class TimeSlot(
    var timeSlotString: String? = null
): Serializable {

    override fun toString(): String {
        return "Video{" +
                "Time Slot=" + timeSlotString +
                '}'
    }

    companion object {
        internal const val serialVersionUID = 727566175075960653L
    }
}
