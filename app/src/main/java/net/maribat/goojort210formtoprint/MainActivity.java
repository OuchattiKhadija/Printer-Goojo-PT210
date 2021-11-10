package net.maribat.goojort210formtoprint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private PrinterInstance mPrinter;
    private Context mContext;
    BluetoothPrinterController bluetoothPrinterController;

    Button print_btn, print_img;
    EditText editText1, editText2;
    Bitmap bitmap;
    String formattedDate, formattedTime;
    private static OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        print_btn = findViewById(R.id.print_btn);
        print_img = findViewById(R.id.print_img);
        mContext = this;

        editText1 = findViewById(R.id.edit_txt1);
        editText2 = findViewById(R.id.edit_txt2);


        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        mContext = this;
        bluetoothPrinterController = new BluetoothPrinterController(this, this, mHandler);

        bluetoothPrinterController.connectToPrt();
        print_btn.setOnClickListener(view -> {
           /* String txt = "Client  : " + editText1.getText().toString() + " \n\n"
                    + "Montant : " + editText2.getText().toString() + " Dhs\n\n\n\n";

*/
            getCurrentDate();
            getCurrentTime();


            String client_ed = editText1.getText().toString();
            String montant_ed = editText2.getText().toString();
            mPrinter.setPrintModel(false, false,
                    true, false);
            printCompanyName(mPrinter);
            printDate(mPrinter, formattedDate, formattedTime);
            printIputs(mPrinter, client_ed, montant_ed);
            printThanks(mPrinter);
        });


    }


    private void getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        formattedDate = df.format(c);
    }

    private void getCurrentTime() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        formattedTime = df.format(c);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    mPrinter = bluetoothPrinterController.getPrinter();
                    break;
                case PrinterConstants.Connect.FAILED:

                    Toast.makeText(mContext, "connect failed...", Toast.LENGTH_SHORT).show();
                    break;
                case PrinterConstants.Connect.CLOSED:

                    Toast.makeText(mContext, "connect close...", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }

    };

/*    public static void printTable(PrinterInstance mPrinter, boolean is58mm, String client, String montant) {
        mPrinter.init();

        mPrinter.setCharacterMultiple(0, 0);
        String column = "Client;Montant";
        Table table;
        if (is58mm) {
            table = new Table(column, ";", new int[]{14, 9, 6, 3});
        } else {
            table = new Table(column, ";", new int[]{16, 8, 8, 12});
        }

        table.setColumnAlignRight(true);
        table.addRow(client
                + ";" + montant + ";DHs");

        mPrinter.printTable(table);

        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
    }*/

    public static void printImage(PrinterInstance mPrinter, Bitmap btm) {
        mPrinter.init();
        // mPrinter.setFont(0, 0, 0, 0);
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
        //  mPrinter.printText(resources.getString(R.string.str_image));
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printImage(btm);
        mPrinter.printText("\n\n\n\n");                     //换4行


    }

    public static void printIputs(PrinterInstance mPrinter, String client, String montant) {
        mPrinter.init();

        mPrinter.printText("\nClient  :  " + client + "\n\n");
        mPrinter.printText("Montant :  " + montant + "\n");
        mPrinter.printText("--------------------------------\n");
        // mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        //
    }

    public static void printCompanyName(PrinterInstance mPrinter) {
        mPrinter.init();
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, 1);
        mPrinter.setPrintModel(true, true, true, false);
        mPrinter.printText("\n\nMARIBAT");
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);//
    }

    public static void printDate(PrinterInstance mPrinter, String date, String heure) {
        mPrinter.init();
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, 1);
        mPrinter.setPrintModel(false, false, false, false);
        mPrinter.printText("Parc Industriel  Sapino Nouaceur,Casablanca\n");
        mPrinter.printText("+212 522 014 036\n");
        mPrinter.printText("================================\n");
        mPrinter.printText("Date : " + date + "\n");
        mPrinter.printText("Heure : " + heure + "\n");
        mPrinter.printText("================================");
        // mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);//
    }

    public static void printThanks(PrinterInstance mPrinter) {
        mPrinter.init();
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, 1);
        mPrinter.setPrintModel(false, true, true, false);
        mPrinter.printText("Merci\n\n");
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);//
    }


}