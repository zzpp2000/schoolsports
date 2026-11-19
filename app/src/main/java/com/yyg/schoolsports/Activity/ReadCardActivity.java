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

import com.yyg.schoolsports.R;
import com.yyg.schoolsports.utils.MediaManager;
import com.yyg.schoolsports.utils.Nfc;
import com.yyg.schoolsports.utils.Scanner;
import com.yyg.schoolsports.utils.Utils;
import com.yyg.schoolsports.utils.XmlUtil;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class ReadCardActivity extends AppCompatActivity {
    private static final String TAG = "ReadCardActivity";
    private PendingIntent mPendingIntent;
    private NfcAdapter mNfcAdapter;
    private String[][] mTechLists;
    private IntentFilter[] mFilters;
    private Tag mTag;
    private Scanner mScanner;
    private String testid;
    Nfc nfc = new Nfc();

    public String readcardpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/voice/请读卡.wav";
    public String readqrpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/voice/请刷二维码.wav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readcard);

        Intent intent =getIntent();
        testid = intent.getStringExtra("testitem");
        Log.i(TAG,testid);

        initScanner();

        MediaManager.playSound(ReadCardActivity.this, readcardpath, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                MediaManager.playSound(ReadCardActivity.this, readqrpath, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        MediaManager.reset();
                    }
                });
            }
        });

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
                readMifareClassic(mTag);
                break;
            }
        }
    }

    private void readMifareClassic(Tag tag) {
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
                Log.i(TAG,testid);
                if(testid.equals("HWB")){
                    Intent intent = new Intent(ReadCardActivity.this, HeightWTestActivity.class);
                    intent.putExtra("testcardid",CardId+"");
                    intent.putExtra("TESTMODE", 0);  //刷卡
                    startActivity(intent);
                }else if(testid.equals("VC")){
                    Intent intent = new Intent(ReadCardActivity.this, VitalcapacityTestActivity.class);
                    intent.putExtra("testcardid",CardId+"");
                    intent.putExtra("TESTMODE", 0);   //刷卡
                    startActivity(intent);
                }else if(testid.equals("50M")){
                    Intent intent = new Intent(ReadCardActivity.this, VitalcapacityTestActivity.class);
                    intent.putExtra("testcardid",CardId+"");
                    intent.putExtra("TESTMODE", 0);   //刷卡
                }else if(testid.equals("SFB")){
                    Intent intent = new Intent(ReadCardActivity.this, SFBTestActivity.class);
                    intent.putExtra("testcardid",CardId+"");
                    intent.putExtra("TESTMODE", 0);   //刷卡
                    startActivity(intent);
                }else if(testid.equals("SU")){
                    Intent intent = new Intent(ReadCardActivity.this, SuTestActivity.class);
                    intent.putExtra("testcardid",CardId+"");
                    intent.putExtra("TESTMODE", 0);   //刷卡
                    startActivity(intent);
                }else if(testid.equals("SLJ")){
                    Intent intent = new Intent(ReadCardActivity.this, SLJTestActivity.class);
                    intent.putExtra("testcardid",CardId+"");
                    intent.putExtra("TESTMODE", 0);   //刷卡
                    startActivity(intent);
                }else if(testid.equals("VJUMP")){
                    Intent intent = new Intent(ReadCardActivity.this, VJUMPTestActivity.class);
                    intent.putExtra("testcardid",CardId+"");
                    intent.putExtra("TESTMODE", 0);   //刷卡
                    startActivity(intent);
                }else if(testid.equals("PLU")){
                    Intent intent = new Intent(ReadCardActivity.this, PLUTestActivity.class);
                    intent.putExtra("testcardid",CardId+"");
                    intent.putExtra("TESTMODE", 0);   //刷卡
                    startActivity(intent);
                }else if(testid.equals("MDB")){
                    Intent intent = new Intent(ReadCardActivity.this, MedballTestActivity.class);
                    intent.putExtra("testcardid",CardId+"");
                    intent.putExtra("TESTMODE", 0);   //刷卡
                    startActivity(intent);
                }else if(testid.equals("SGJ")){
                    Intent intent = new Intent(ReadCardActivity.this, SargJumpTestActivity.class);
                    intent.putExtra("testcardid",CardId+"");
                    intent.putExtra("TESTMODE", 0);   //刷卡
                    startActivity(intent);
                }

                finish();

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

    private void initScanner() {
        mScanner = new Scanner(new Scanner.onScanResultListener() {
            public void onScanResult(String result) {
                Log.i(TAG,result);
                if(testid.equals("HWB")){
                    Intent intent = new Intent(ReadCardActivity.this, HeightWTestActivity.class);
                    intent.putExtra("testcardid",result);
                    intent.putExtra("TESTMODE", 1);  //二维码
                    startActivity(intent);
                    finish();
                }else if(testid.equals("VC")){
                    Intent intent = new Intent(ReadCardActivity.this, VitalcapacityTestActivity.class);
                    intent.putExtra("testcardid",result);
                    intent.putExtra("TESTMODE", 1);   //二维码
                    startActivity(intent);
                    finish();
                }else if(testid.equals("50M")){
                    Intent intent = new Intent(ReadCardActivity.this, VitalcapacityTestActivity.class);
                    intent.putExtra("testcardid",result);
                    intent.putExtra("TESTMODE", 1);   //二维码
                    startActivity(intent);
                    finish();
                }else if(testid.equals("SFB")){
                    Intent intent = new Intent(ReadCardActivity.this, SFBTestActivity.class);
                    intent.putExtra("testcardid",result);
                    intent.putExtra("TESTMODE", 1);   //二维码
                    startActivity(intent);
                    finish();
                }else if(testid.equals("SU")){
                    Intent intent = new Intent(ReadCardActivity.this, SuTestActivity.class);
                    intent.putExtra("testcardid",result);
                    intent.putExtra("TESTMODE", 1);   //二维码
                    startActivity(intent);
                    finish();
                }else if(testid.equals("SLJ")){
                    Intent intent = new Intent(ReadCardActivity.this, SLJTestActivity.class);
                    intent.putExtra("testcardid",result);
                    intent.putExtra("TESTMODE", 1);   //二维码
                    startActivity(intent);
                    finish();
                }else if(testid.equals("VJUMP")){
                    Intent intent = new Intent(ReadCardActivity.this, VJUMPTestActivity.class);
                    intent.putExtra("testcardid",result);
                    intent.putExtra("TESTMODE", 1);   //二维码
                    startActivity(intent);
                    finish();
                }else if(testid.equals("PLU")){
                    Intent intent = new Intent(ReadCardActivity.this, PLUTestActivity.class);
                    intent.putExtra("testcardid",result);
                    intent.putExtra("TESTMODE", 1);   //二维码
                    startActivity(intent);
                    finish();
                }else if(testid.equals("MDB")){
                    Intent intent = new Intent(ReadCardActivity.this, MedballTestActivity.class);
                    intent.putExtra("testcardid",result);
                    intent.putExtra("TESTMODE", 1);   //二维码
                    startActivity(intent);
                    finish();
                }else if(testid.equals("SGJ")){
                    Intent intent = new Intent(ReadCardActivity.this, SargJumpTestActivity.class);
                    intent.putExtra("testcardid",result);
                    intent.putExtra("TESTMODE", 1);   //二维码
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
