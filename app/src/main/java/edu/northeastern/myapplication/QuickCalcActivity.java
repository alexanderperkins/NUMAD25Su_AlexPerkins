package edu.northeastern.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class QuickCalcActivity extends AppCompatActivity {

    private TextView calcTextView;
    private StringBuilder expression = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_calc);

        calcTextView = findViewById(R.id.calcTextView);

        // IDs of buttons for digits and operators (+ and -)
        int[] buttonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnPlus, R.id.btnMinus
        };

        // Common listener for digit and operator buttons
        View.OnClickListener inputListener = v -> {
            Button b = (Button) v;
            expression.append(b.getText());
            calcTextView.setText(expression.toString());
        };

        // Attach listener to all digit/operator buttons
        for (int id : buttonIds) {
            Button b = findViewById(id);
            if (b != null) {
                b.setOnClickListener(inputListener);
            }
        }

        // Delete button handler
        Button btnDel = findViewById(R.id.btnDel);
        if (btnDel != null) {
            btnDel.setOnClickListener(v -> {
                if (expression.length() > 0) {
                    expression.deleteCharAt(expression.length() - 1);
                    calcTextView.setText(expression.length() == 0 ? "0" : expression.toString());
                }
            });
        }

        // Equal button handler
        Button btnEqual = findViewById(R.id.btnEqual);
        if (btnEqual != null) {
            btnEqual.setOnClickListener(v -> {
                if (expression.length() == 0) {
                    calcTextView.setText("0");
                    return;
                }
                try {
                    int result = evaluate(expression.toString());
                    calcTextView.setText(String.valueOf(result));
                    expression = new StringBuilder(String.valueOf(result));
                } catch (Exception e) {
                    calcTextView.setText("ERR");
                    expression.setLength(0);
                }
            });
        }
    }

    // Simple evaluator supporting only + and - operators, left to right
    private int evaluate(String expr) {
        int result = 0;
        // Split while preserving + and - as tokens
        String[] tokens = expr.split("(?=[+-])|(?<=[+-])");
        int i = 0;
        while (i < tokens.length) {
            String token = tokens[i];
            if (token.equals("+") || token.equals("-")) {
                int next = Integer.parseInt(tokens[i + 1]);
                result = token.equals("+") ? result + next : result - next;
                i += 2;
            } else {
                result = Integer.parseInt(token);
                i++;
            }
        }
        return result;
    }
}
