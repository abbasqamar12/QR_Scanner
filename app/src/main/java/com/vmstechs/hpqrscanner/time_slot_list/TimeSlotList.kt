package com.vmstechs.hpqrscanner.time_slot_list


object TimeSlotList {
    val timeSlotList: List<TimeSlot> by lazy {
        setupStates()
    }

    private fun setupStates(): List<TimeSlot> {
        val timeSlotName = arrayOf(
            "Bluewave Session: 10:30 AM - 11:30 AM",
            "Sanbay Session: 11:45 AM - 12:45 PM",
            "Cache Session: 2:00 PM - 3:00 PM",
            "Microcare Session: 3:15 PM - 4:15 PM",
            "Monamit Session: 4:30 PM - 5:30 PM",
            "Variety Session: 5:45 PM - 6:45 PM",
            )


        val timeSlotList = timeSlotName.indices.map {
            buildStateInfo(
                timeSlotName[it],

                )
        }

        return timeSlotList
    }

    private fun buildStateInfo(
        stateName: String,
    ): TimeSlot {
        val timeSlotList = TimeSlot()
        timeSlotList.timeSlotString = stateName
        return timeSlotList
    }

}