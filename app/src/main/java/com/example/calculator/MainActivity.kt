package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private lateinit var tvExpression: TextView

    private var currentInput = ""
    private var operand1: Double? = null
    private var pendingOperator: String? = null
    private var justCalculated = false

    private val df = DecimalFormat("#.##########")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)
        tvExpression = findViewById(R.id.tvExpression)

        // Number buttons
        val numberButtons = listOf(
            R.id.btn0 to "0",
            R.id.btn1 to "1",
            R.id.btn2 to "2",
            R.id.btn3 to "3",
            R.id.btn4 to "4",
            R.id.btn5 to "5",
            R.id.btn6 to "6",
            R.id.btn7 to "7",
            R.id.btn8 to "8",
            R.id.btn9 to "9",
            R.id.btnDot to "."
        )

        numberButtons.forEach { (id, value) ->
            findViewById<Button>(id).setOnClickListener { onNumberClick(value) }
        }

        // Operator buttons
        findViewById<Button>(R.id.btnPlus).setOnClickListener { onOperatorClick("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { onOperatorClick("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperatorClick("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperatorClick("÷") }

        // Other buttons
        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualsClick() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { onClearClick() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { onBackspaceClick() }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { onPercentClick() }
    }

    private fun onNumberClick(value: String) {
        if (justCalculated) {
            currentInput = ""
            justCalculated = false
        }

        if (value == "." && currentInput.contains(".")) return
        if (value == "0" && currentInput == "0") return
        if (currentInput == "0" && value != ".") {
            currentInput = value
        } else {
            currentInput += value
        }

        updateDisplay()
    }

    private fun onOperatorClick(op: String) {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull() ?: return

            if (operand1 != null && pendingOperator != null && !justCalculated) {
                val result = calculate(operand1!!, value, pendingOperator!!)
                operand1 = result
                currentInput = ""
                tvDisplay.text = format(result)
            } else {
                operand1 = value
                currentInput = ""
            }
        }

        pendingOperator = op
        justCalculated = false
        updateExpression()
    }

    private fun onEqualsClick() {
        if (operand1 == null || pendingOperator == null || currentInput.isEmpty()) return

        val value = currentInput.toDoubleOrNull() ?: return
        val result = calculate(operand1!!, value, pendingOperator!!)

        tvExpression.text = "${format(operand1!!)} $pendingOperator ${format(value)} ="
        tvDisplay.text = format(result)

        currentInput = format(result)
        operand1 = null
        pendingOperator = null
        justCalculated = true
    }

    private fun onClearClick() {
        currentInput = ""
        operand1 = null
        pendingOperator = null
        justCalculated = false
        tvDisplay.text = "0"
        tvExpression.text = ""
    }

    private fun onBackspaceClick() {
        if (justCalculated) return
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            updateDisplay()
        }
    }

    private fun onPercentClick() {
        if (currentInput.isEmpty()) return
        val value = currentInput.toDoubleOrNull() ?: return
        currentInput = format(value / 100)
        updateDisplay()
    }

    private fun calculate(a: Double, b: Double, op: String): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> if (b == 0.0) Double.NaN else a / b
            else -> b
        }
    }

    private fun format(value: Double): String {
        return if (value.isNaN()) "Error" else df.format(value)
    }

    private fun updateDisplay() {
        tvDisplay.text = if (currentInput.isEmpty()) "0" else currentInput
        updateExpression()
    }

    private fun updateExpression() {
        val op1 = operand1?.let { format(it) } ?: ""
        val op = pendingOperator ?: ""
        tvExpression.text = "$op1 $op".trim()
    }
}
