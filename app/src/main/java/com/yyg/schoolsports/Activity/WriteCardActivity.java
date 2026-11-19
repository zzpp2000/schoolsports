package com.yyg.schoolsports.Activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcBarcode;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.yyg.schoolsports.R;
import com.yyg.schoolsports.utils.MediaManager;
import com.yyg.schoolsports.utils.Nfc;
import com.yyg.schoolsports.utils.PlayAudio;
import com.yyg.schoolsports.utils.Scanner;
import com.yyg.schoolsports.utils.Utils;

import java.io.IOException;
import androidx.appcompat.app.AppCompatActivity;

public class WriteCardActivity extends AppCompatActivity {
    private static final String TAG = "WriteCardActivity";
    private PendingIntent mPendingIntent;
    private NfcAdapter mNfcAdapter;
    private String[][] mTechLists;
    private IntentFilter[] mFilters;
    private Tag mTag;
    private Scanner mScanner;
    private String testid;
    private String testresult;
    private String testitem;
    private String testhei;     //测试结果身高
    private String testwei;     //测试结果体重
    private String testbmi;     //测试结果BMI
    Nfc nfc = new Nfc();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writecard);

        Intent intent =getIntent();
        testitem = intent.getStringExtra("testitem");
        testid = intent.getStringExtra("testcardid");
        if(testitem.equals("HWB")){
            testhei = intent.getStringExtra("testheight");
            testwei = intent.getStringExtra("testweight");
            testbmi = intent.getStringExtra("testbmi");
        }else if(testitem.equals("VC")){
            testresult = intent.getStringExtra("testresult");
        }else if(testitem.equals("SFB")){
            testresult = intent.getStringExtra("testresult");
        }else if(testitem.equals("SU")){
            testresult = intent.getStringExtra("testresult");
        }else if(testitem.equals("SLJ")){
            testresult = intent.getStringExtra("testresult");
        }else if(testitem.equals("VJUMP")){
            testresult = intent.getStringExtra("testresult");
        }else if(testitem.equals("PLU")){
            testresult = intent.getStringExtra("testresult");
        }else if(testitem.equals("MDB")){
            testresult = intent.getStringExtra("testresult");
        }else if(testitem.equals("SGJ")){
            testresult = intent.getStringExtra("testresult");
        }

        PlayAudio.playaudio(2);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            // mToast.showToast(R.string.nfc_not_support);
        } else {
            if (!mNfcAdapter.isEnabled()) {
                //mToast.showToast(R.string.nfc_disabled);
            } else {
                // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
                // will fill in the intent with the details of the discovered tag before delivering to
                // this activity.
                mPendingIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

                // Setup an intent filter for all MIME based dispatches
                IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
                try {
                    ndef.addDataType("*/*");
                } catch (IntentFilter.MalformedMimeTypeException e) {
                    throw new RuntimeException("fail", e);
                }
                mFilters = new IntentFilter[]{
                        ndef,
                };

                // Setup a tech list for all Ndef tags
                mTechLists = new String[][]{
                        new String[]{IsoDep.class.getName()},
                        new String[]{MifareClassic.class.getName()},
                        new String[]{MifareUltralight.class.getName()},
                        new String[]{Ndef.class.getName()},
                        new String[]{NfcA.class.getName()},
                        new String[]{NfcB.class.getName()},
                        new String[]{NfcBarcode.class.getName()},
                        new String[]{NfcF.class.getName()},
                        new String[]{NfcV.class.getName()},
                };
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mNfcAdapter != null && mPendingIntent != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.i(TAG, "Discovered tag with intent: " + intent);
        mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String[] techList = mTag.getTechList();
        for(int i=0;i<techList.length;i++) {
            Log.d(TAG,techList[i]);
            if (techList[i].equals(MifareClassic.class.getName())) {
                writeMifareClassic(mTag);
                break;
            }
        }
    }

    private void writeMifareClassic(Tag tag) {
        MifareClassic mc = MifareClassic.get(tag);
        try {
            boolean auth = false;
            StringBuilder metaInfo = new StringBuilder();
            mc.connect();
            int type = mc.getType();
            int sectorCount = mc.getSectorCount();
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
            }

            metaInfo.append("card type: ").append(typeS).append(" sectors: ").append(sectorCount).append(" blocks: ").append(mc.getBlockCount()).append(" size: ").append(mc.getSize()).append("byte\n");
            auth = mc.authenticateSectorWithKeyA(1, MifareClassic.KEY_DEFAULT);
            if (auth) {
                metaInfo.append("Sector ").append(1).append(":auth ok\n");
                byte[] data = mc.readBlock(4);
                metaInfo.append("Block ").append(4).append(" : ").append(Utils.bytesToHexString(data)).append("\n");
                int CardId = (data[0]&0xff)*256 + (data[1]&0xff);
                if(testid.equals(Integer.toString(CardId))){
                    if(testitem.equals("VC")) {
                        DateWrite(Integer.parseInt(testresult), mc);
                        byte[] data5 = mc.readBlock(5);
                        metaInfo.append("Block ").append(5).append(" : ").append(Utils.bytesToHexString(data5)).append("\n");
                        finish();
                    }else if(testitem.equals("HWB")){
                        BmiWrite(Integer.parseInt(testhei),Integer.parseInt(testwei),Integer.parseInt(testbmi),mc);
                        byte[] data5 = mc.readBlock(5);
                        metaInfo.append("Block ").append(5).append(" : ").append(Utils.bytesToHexString(data5)).append("\n");
                        finish();
                    }else if(testitem.equals("SFB")) {
                        DateWrite(Integer.parseInt(testresult), mc);
                        byte[] data5 = mc.readBlock(5);
                        metaInfo.append("Block ").append(5).append(" : ").append(Utils.bytesToHexString(data5)).append("\n");
                        finish();
                    }else if(testitem.equals("SU")) {
                        DateWrite(Integer.parseInt(testresult), mc);
                        byte[] data5 = mc.readBlock(5);
                        metaInfo.append("Block ").append(5).append(" : ").append(Utils.bytesToHexString(data5)).append("\n");
                        finish();
                    }else if(testitem.equals("SLJ")) {
                        DateWrite(Integer.parseInt(testresult), mc);
                        byte[] data5 = mc.readBlock(5);
                        metaInfo.append("Block ").append(5).append(" : ").append(Utils.bytesToHexString(data5)).append("\n");
                        finish();
                    }else if(testitem.equals("VJUMP")) {
                        DateWrite(Integer.parseInt(testresult), mc);
                        byte[] data5 = mc.readBlock(5);
                        metaInfo.append("Block ").append(5).append(" : ").append(Utils.bytesToHexString(data5)).append("\n");
                        finish();
                    }else if(testitem.equals("PLU")) {
                        DateWrite(Integer.parseInt(testresult), mc);
                        byte[] data5 = mc.readBlock(5);
                        metaInfo.append("Block ").append(5).append(" : ").append(Utils.bytesToHexString(data5)).append("\n");
                        finish();
                    }else if(testitem.equals("MDB")) {
                        DateWrite(Integer.parseInt(testresult), mc);
                        byte[] data5 = mc.readBlock(5);
                        metaInfo.append("Block ").append(5).append(" : ").append(Utils.bytesToHexString(data5)).append("\n");
                        finish();
                    }else if(testitem.equals("SGJ")) {
                        DateWrite(Integer.parseInt(testresult), mc);
                        byte[] data5 = mc.readBlock(5);
                        metaInfo.append("Block ").append(5).append(" : ").append(Utils.bytesToHexString(data5)).append("\n");
                        finish();
                    }
                }else{
                    Toast.makeText(WriteCardActivity.this,"卡号不符",Toast.LENGTH_SHORT).show();
                }

            }
            Log.d(TAG, metaInfo.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                mc.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getDeviceId() > 0 && !event.isSystem())
            return mScanner.handleKey(event);
        else
            return super.dispatchKeyEvent(event);
    }

    public void DateWrite(int data,MifareClassic mifareClassic){
        byte[] datawrite = new byte[16];

        datawrite[0] = 0x00;
        datawrite[1] = 0x00;
        datawrite[2] = (byte)(data/256);
        datawrite[3] = (byte)(data%256);
        datawrite[4] = 0x00;
        datawrite[5] = 0x00;
        datawrite[6] = 0x00;
        datawrite[7] = 0x00;
        datawrite[8] = 0x00;
        datawrite[9] = 0x00;
        datawrite[10] = 0x00;
        datawrite[11] = 0x00;
        datawrite[12] = 0x00;
        datawrite[13] = 0x00;
        datawrite[14] = 0x00;
        datawrite[15] = 0x00;

        try {
            mifareClassic.writeBlock(5,datawrite);
        }catch (IOException e) {
        }
    }

    public void BmiWrite(int height,int weight,int bmi,MifareClassic mifareClassic){
        byte[] datawrite = new byte[16];

        datawrite[0] = 0x00;
        datawrite[1] = 0x00;
        datawrite[2] = (byte)(height/256);
        datawrite[3] = (byte)(height%256);
        datawrite[4] = (byte)(weight/256);
        datawrite[5] = (byte)(weight%256);
        datawrite[6] = (byte)(bmi/256);
        datawrite[7] = (byte)(bmi%256);
        datawrite[8] = 0x00;
        datawrite[9] = 0x00;
        datawrite[10] = 0x00;
        datawrite[11] = 0x00;
        datawrite[12] = 0x00;
        datawrite[13] = 0x00;
        datawrite[14] = 0x00;
        datawrite[15] = 0x00;

        try {
            mifareClassic.writeBlock(5,datawrite);
        }catch (IOException e) {
        }
    }

}
