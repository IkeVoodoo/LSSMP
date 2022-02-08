class test {
/*
    @Test
    fun test1() {
        println(parseBanTime("00:00:10.0000"))
    }

    fun parseBanTime(time: String): Long {
        val time = DateTimeFormatter.ofPattern("HH:mm:ss.SSSS").parse(time).query(TemporalQueries.localTime())
        return ((time.hour.hours.toLong(DurationUnit.MILLISECONDS) + time.minute.minutes.toLong(DurationUnit.MILLISECONDS) + time.second.seconds.toLong(
            DurationUnit.MILLISECONDS) + (time.nano / 100000).toLong()).toFloat() / 1000 * 20).toLong()
    }*/

}

private operator fun Int.plus(s: String): String {
    return this.toString() + s
}
