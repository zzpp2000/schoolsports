package com.yyg.schoolsports.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.util.Log;
import android.widget.Toast;

import com.yyg.schoolsports.R;

import java.io.IOException;


public class Nfc {
    private static final String TAG = "Nfc";
    private Tag mTag;


    public void ToAdapter(Context context,NfcAdapter nfcAdapter,IntentFilter[] filters,String[][] techLists) {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter == null) {
            Toast.makeText(context,"nfc not support",Toast.LENGTH_LONG).show();
        } else {
            if (!nfcAdapter.isEnabled()) {
                Toast.makeText(context,"NFC disabled",Toast.LENGTH_LONG).show();
            } else {
                // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
                // will fill in the intent with the details of the discovered tag before delivering to
                // this activity.
                Log.i(TAG,"123456");
                // Setup an intent filter for all MIME based dispatches
                IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
                try {
                    ndef.addDataType("*/*");
                } catch (IntentFilter.MalformedMimeTypeException e) {
                    throw new RuntimeException("fail", e);
                }
                filters = new IntentFilter[]{
                        ndef,
                };

                // Setup a tech list for all Ndef tags
                techLists = new String[][]{
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

    public void newInt(Intent intent){
        Log.i(TAG, "Discovered tag with intent: " + intent);
        mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String[] techList = mTag.getTechList();
        Log.d(TAG,mTag.getTechList().length+"");
        for(int i=0;i<techList.length;i++) {
            Log.d(TAG,techList[i]);
            if (techList[i].equals(MifareClassic.class.getName())) {
                readMifareClassic(mTag);
                break;
            } else {
                Log.d(TAG,"TODO: need parser!");
                break;
            }
        }



    }

    public void readMifareClassic(Tag tag) {
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
                byte[] data1 = mc.readBlock(5);
                metaInfo.append("Block ").append(5).append(" : ").append(Utils.bytesToHexString(data1)).append("\n");
                byte[] datawrite =  {(byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x33, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01};
                mc.writeBlock(5,datawrite);
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
}
