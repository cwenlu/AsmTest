package com.example.asmtest

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn1).setOnClickListener {
            var s = "wu"
            if (s == "wu") {
                s = "aa"
            }
            Thread.sleep(100)
        }

        findViewById<Button>(R.id.btn2).setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var sd = "sada"
                if (sd == "wu") {
                    sd = "aa"
                }
                Thread.sleep(50)
            }

        })
        findViewById<Button>(R.id.btn3).setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
            }

        })

        //println(measureDiscount(1, PriceInfo("5", null, "10"), "100"))
        //println(measureDiscount(1, PriceInfo("5", null, "10"), "90"))
        //println(measureDiscount(1, PriceInfo("5", null, "10"), "83"))
        //println(measureDiscount(1, PriceInfo("1.5", null, "10"), "100"))
        //println(measureDiscount(1, PriceInfo("1.5", null, "10"), "80"))
        //println(measureDiscount(2, PriceInfo(null, "7.5", null), "80"))
        //println(measureDiscount(2, PriceInfo(null, "5", null), "80"))
        //println(measureDiscount(2, PriceInfo(null, "5", null), "75.55"))

    }

    //@RequiresApi(Build.VERSION_CODES.N)
    //fun measureDiscount(voucherType: Int, priceInfo: PriceInfo, price: String): BigDecimal {
    //    val zero = BigDecimal(0)
    //    val target = BigDecimal(price)
    //    when (voucherType) {
    //        1 -> {
    //            val fullAmount = BigDecimal(priceInfo?.full_amount ?: return zero)
    //            val pct = target.divide(fullAmount).toInt()
    //            return BigDecimal(priceInfo?.amount ?: return zero).multiply(BigDecimal(pct))
    //                .setScale(2, BigDecimal.ROUND_HALF_UP)
    //        }
    //        2 -> {
    //            val discountPct = BigDecimal(priceInfo?.discount ?: return zero)
    //            return target.multiply(discountPct).divide(BigDecimal(10))
    //                .setScale(2, BigDecimal.ROUND_HALF_UP)
    //        }
    //    }
    //    return zero
    //}
    //
    //@Keep
    //data class PriceInfo(
    //    /**
    //     * 金额
    //     */
    //    var amount: String? = null,
    //    /**
    //     * 折扣   例如 7折，字段只返回7
    //     */
    //    var discount: String? = null,
    //    /**
    //     * 满减： 列如 满10减1. 该字段返回10，金额字段返回1
    //     */
    //    var full_amount: String? = null
    //)
}