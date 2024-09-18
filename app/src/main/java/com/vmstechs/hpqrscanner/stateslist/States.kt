package com.vmstechs.hpqrscanner.stateslist

import java.io.Serializable

data class States(
    var age: String? = null,
    var state_name: String? = null
): Serializable {

    override fun toString(): String {
        return "Video{" +
                "StateName=" + state_name +
                ", LDA='" + age + '\'' +
                '}'
    }

    companion object {
        internal const val serialVersionUID = 727566175075960653L
    }
}
