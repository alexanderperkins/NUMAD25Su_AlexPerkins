    package edu.northeastern.myapplication;

    import android.app.AlertDialog;
    import android.os.Bundle;
    import android.os.Handler;
    import android.os.Looper;
    import android.widget.Button;
    import android.widget.Switch;
    import android.widget.TextView;
    import androidx.appcompat.app.AppCompatActivity;

    public class PrimeSearchActivity extends AppCompatActivity {

        private Button btnStart, btnStop;
        private Switch pacifierSwitch;
        private TextView currentNumberText, latestPrimeText;

        private volatile boolean isRunning = false;
        private Thread primeThread;
        private int current = 3;
        private int latestPrime = 2;

        private Handler handler;

        private static final String PACIFIER_KEY = "PACIFIER_SWITCH";
        private static final String IS_RUNNING_KEY = "IS_RUNNING";
        private static final String CURRENT_KEY = "CURRENT";
        private static final String LATEST_PRIME_KEY = "LATEST_PRIME";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_prime_search);

            btnStart = findViewById(R.id.btnFindPrimes);
            btnStop = findViewById(R.id.btnTerminate);
            pacifierSwitch = findViewById(R.id.pacifierSwitch);
            currentNumberText = findViewById(R.id.textCurrent);
            latestPrimeText = findViewById(R.id.textLatestPrime);

            handler = new Handler(Looper.getMainLooper());

            if (savedInstanceState != null) {
                pacifierSwitch.setChecked(savedInstanceState.getBoolean(PACIFIER_KEY, false));
                isRunning = savedInstanceState.getBoolean(IS_RUNNING_KEY, false);
                current = savedInstanceState.getInt(CURRENT_KEY, 3);
                latestPrime = savedInstanceState.getInt(LATEST_PRIME_KEY, 2);

                currentNumberText.setText("Checking: " + current);
                latestPrimeText.setText("Latest Prime: " + latestPrime);

                if (isRunning) {
                    primeThread = new Thread(this::findPrimes);
                    primeThread.start();
                }
            }

            btnStart.setOnClickListener(v -> {
                if (!isRunning) {
                    isRunning = true;
                    primeThread = new Thread(this::findPrimes);
                    primeThread.start();
                }
            });

            btnStop.setOnClickListener(v -> stopSearch());
        }

        private void findPrimes() {
            current = 3;
            while (isRunning) {
                final int n = current;
                if (isPrime(n)) {
                    latestPrime = n;
                    handler.post(() -> {
                        currentNumberText.setText("Checking: " + n);
                        latestPrimeText.setText("Latest Prime: " + n);
                    });
                } else {
                    handler.post(() -> currentNumberText.setText("Checking: " + n));
                }
                current += 2;
            }
        }

        private boolean isPrime(int n) {
            for (int i = 2; i <= Math.sqrt(n); i++) {
                if (n % i == 0) return false;
            }
            return true;
        }

        private void stopSearch() {
            isRunning = false;
            if (primeThread != null) {
                primeThread.interrupt();
                primeThread = null;
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putBoolean(PACIFIER_KEY, pacifierSwitch.isChecked());
            outState.putBoolean(IS_RUNNING_KEY, isRunning);
            outState.putInt(CURRENT_KEY, current);
            outState.putInt(LATEST_PRIME_KEY, latestPrime);
        }


        @Override
        public void onBackPressed() {
            if (isRunning) {
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Exit")
                        .setMessage("Search is running. Terminate and exit?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            stopSearch();
                            finish();
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                super.onBackPressed();
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            stopSearch(); // ensure thread is stopped
        }
    }
