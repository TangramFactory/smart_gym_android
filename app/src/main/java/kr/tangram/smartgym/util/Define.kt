package kr.tangram.smartgym.util

class Define {

    interface Preferences {
        companion object {
        }
    }

    interface AppData {
        companion object {
            const val ScreenLock = "ScreenLock"
        }
    }

    interface BusEvent{
        companion object {
            const val DeviceState = "DeviceState"
            const val HistorySync = "HistorySync"
        }
    }

    interface DeviceInfo {
        interface Type {
            companion object {
                const val Rookie = "Rookie"
                const val Led = "Led"
            }
        }
    }


    interface Extra {
        companion object {
            const val Identifier = "Identifier"
        }
    }

    interface ResCode {
        companion object {
            const val SUCCESS = 0
        }
    }

    interface HardCording{
        companion object {
            const val Weight = 65f
            const val Uid = "InYNMt8FgISN6G44jf0p6fqcCNr2"
        }
    }
}