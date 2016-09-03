package model

sealed trait DBConf
case object CrazyCatLadyDb extends DBConf
case object CatteryDb extends DBConf

