package utils

object Utils {
    def currencyFormat(num:Int) :String={
        val locale = new java.util.Locale("ko","KR")
        val formatter = java.text.NumberFormat.getIntegerInstance(locale)
        formatter.format(num)
    }
}
