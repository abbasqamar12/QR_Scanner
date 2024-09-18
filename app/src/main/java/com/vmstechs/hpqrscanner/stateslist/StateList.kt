package com.vmstechs.hpqrscanner.stateslist

import com.vmstechs.hpqrscanner.time_slot_list.TimeSlot


object StateList {
    val stateList: List<States> by lazy {
        setupStates()
    }

    private fun setupStates(): List<States> {
        val stateName = arrayOf(
            "Hyderabad",


        )

        val lda = arrayOf(
            "21",

        )


        val stateList = stateName.indices.map {
            buildStateInfo(
                stateName[it],
                lda[it]

            )
        }

        return stateList
    }

    private fun buildStateInfo(
        stateName: String,
        lda: String,


    ): States {
        val stateList = States()
        stateList.state_name = stateName
        stateList.age = lda

        return stateList
    }

}