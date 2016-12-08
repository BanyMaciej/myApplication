package com.mb.myapplication;

/**
 * Created by Maciek on 2016-11-24.
 */
        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.InputStreamReader;
        import java.util.Locale;

        import android.annotation.TargetApi;
        import android.os.Build;
        import android.util.Log;

public class CurrentReader {

    static final String BUILD_MODEL = Build.MODEL.toLowerCase(Locale.ENGLISH);

    @TargetApi(4)
    static public Long getValue() {
        File f = null;

        // Galaxy S3
        if (CurrentReader.BUILD_MODEL.contains("gt-i9300")
                || CurrentReader.BUILD_MODEL.contains("gt-i9300T")
                || CurrentReader.BUILD_MODEL.contains("gt-i9305")
                || CurrentReader.BUILD_MODEL.contains("gt-i9305N")
                || CurrentReader.BUILD_MODEL.contains("gt-i9305T")
                || CurrentReader.BUILD_MODEL.contains("shv-e210k")
                || CurrentReader.BUILD_MODEL.contains("shv-e210l")
                || CurrentReader.BUILD_MODEL.contains("shv-e210s")
                || CurrentReader.BUILD_MODEL.contains("sgh-t999")
                || CurrentReader.BUILD_MODEL.contains("sgh-t999l")
                || CurrentReader.BUILD_MODEL.contains("sgh-t999v")
                || CurrentReader.BUILD_MODEL.contains("sgh-i747")
                || CurrentReader.BUILD_MODEL.contains("sgh-i747m")
                || CurrentReader.BUILD_MODEL.contains("sgh-n064")
                || CurrentReader.BUILD_MODEL.contains("sc-06d")
                || CurrentReader.BUILD_MODEL.contains("sgh-n035")
                || CurrentReader.BUILD_MODEL.contains("sc-03e")
                || CurrentReader.BUILD_MODEL.contains("SCH-j021")
                || CurrentReader.BUILD_MODEL.contains("scl21")
                || CurrentReader.BUILD_MODEL.contains("sch-r530")
                || CurrentReader.BUILD_MODEL.contains("sch-i535")
                || CurrentReader.BUILD_MODEL.contains("sch-S960l")
                || CurrentReader.BUILD_MODEL.contains("gt-i9308")
                || CurrentReader.BUILD_MODEL.contains("sch-i939")
                || CurrentReader.BUILD_MODEL.contains("sch-s968c")) {
            f = new File("/sys/class/power_supply/battery/current_max");
            if (f.exists()) {
                return OneLineReader.getValue(f, false);
            }
        }

        if (CurrentReader.BUILD_MODEL.contains("nexus 7")
                || CurrentReader.BUILD_MODEL.contains("one")
                || CurrentReader.BUILD_MODEL.contains("lg-d851")) {
            f = new File("/sys/class/power_supply/battery/current_now");
            if (f.exists()) {
                return OneLineReader.getValue(f, false);
            }
        }

        if (CurrentReader.BUILD_MODEL.contains("sl930")) {
            f = new File("/sys/class/power_supply/da9052-bat/current_avg");
            if (f.exists()) {
                return OneLineReader.getValue(f, false);
            }
        }

        // Galaxy S4
        if (CurrentReader.BUILD_MODEL.contains("sgh-i337")
                || CurrentReader.BUILD_MODEL.contains("gt-i9505")
                || CurrentReader.BUILD_MODEL.contains("gt-i9500")
                || CurrentReader.BUILD_MODEL.contains("sch-i545")
                || CurrentReader.BUILD_MODEL.contains("find 5")
                || CurrentReader.BUILD_MODEL.contains("sgh-m919")
                || CurrentReader.BUILD_MODEL.contains("sgh-i537")) {
            f = new File("/sys/class/power_supply/battery/current_now");
            if (f.exists()) {
                return OneLineReader.getValue(f, false);
            }
        }

        if (CurrentReader.BUILD_MODEL.contains("cynus")) {
            f = new File(
                    "/sys/devices/platform/mt6329-battery/FG_Battery_CurrentConsumption");
            if (f.exists()) {
                return OneLineReader.getValue(f, false);
            }
        }
        // Zopo Zp900, etc.
        if (CurrentReader.BUILD_MODEL.contains("zp900")
                || CurrentReader.BUILD_MODEL.contains("jy-g3")
                || CurrentReader.BUILD_MODEL.contains("zp800")
                || CurrentReader.BUILD_MODEL.contains("zp800h")
                || CurrentReader.BUILD_MODEL.contains("zp810")
                || CurrentReader.BUILD_MODEL.contains("w100")
                || CurrentReader.BUILD_MODEL.contains("zte v987")) {
            f = new File(
                    "/sys/class/power_supply/battery/BatteryAverageCurrent");
            if (f.exists()) {
                return OneLineReader.getValue(f, false);
            }
        }

        // Samsung Galaxy Tab 2
        if (CurrentReader.BUILD_MODEL.contains("gt-p31")
                || CurrentReader.BUILD_MODEL.contains("gt-p51")) {
            f = new File("/sys/class/power_supply/battery/current_avg");
            if (f.exists()) {
                return OneLineReader.getValue(f, false);
            }
        }

        // trimuph with cm7, lg ls670, galaxy s3, galaxy note 2
        if (CurrentReader.BUILD_MODEL.contains("triumph")
                || CurrentReader.BUILD_MODEL.contains("ls670")
                || CurrentReader.BUILD_MODEL.contains("gt-i9300")
                || CurrentReader.BUILD_MODEL.contains("sm-n9005")
                || CurrentReader.BUILD_MODEL.contains("gt-n7100")
                || CurrentReader.BUILD_MODEL.contains("sgh-i317")) {
            f = new File("/sys/class/power_supply/battery/current_now");
            if (f.exists()) {
                return OneLineReader.getValue(f, false);
            }
        }

        // htc desire hd / desire z / inspire?
        // htc evo view tablet
        if (CurrentReader.BUILD_MODEL.contains("desire hd")
                || CurrentReader.BUILD_MODEL.contains("desire z")
                || CurrentReader.BUILD_MODEL.contains("inspire")
                || CurrentReader.BUILD_MODEL.contains("pg41200")) {
            f = new File("/sys/class/power_supply/battery/batt_current");
            if (f.exists()) {
                return OneLineReader.getValue(f, false);
            }
        }

        // nexus one cyangoenmod
        f = new File("/sys/devices/platform/ds2784-battery/getcurrent");
        if (f.exists()) {
            return OneLineReader.getValue(f, true);
        }

        // sony ericsson xperia x1
        f = new File(
                "/sys/devices/platform/i2c-adapter/i2c-0/0-0036/power_supply/ds2746-battery/current_now");
        if (f.exists()) {
            return OneLineReader.getValue(f, false);
        }

        // xdandroid
		/* if (Build.MODEL.equalsIgnoreCase("MSM")) { */
        f = new File(
                "/sys/devices/platform/i2c-adapter/i2c-0/0-0036/power_supply/battery/current_now");
        if (f.exists()) {
            return OneLineReader.getValue(f, false);
        }
		/* } */

        // some htc devices
        f = new File("/sys/class/power_supply/battery/batt_current_now");
        if (f.exists()) {
            return OneLineReader.getValue(f, true);
        }

        // Nexus One.
        // TODO: Make this not default but specific for N1 because of the normalization.
        f = new File("/sys/class/power_supply/battery/current_now");
        if (f.exists()) {
            return OneLineReader.getValue(f, true);
        }

        // samsung galaxy vibrant
        f = new File("/sys/class/power_supply/battery/batt_chg_current");
        if (f.exists())
            return OneLineReader.getValue(f, false);

        // sony ericsson x10
        f = new File("/sys/class/power_supply/battery/charger_current");
        if (f.exists())
            return OneLineReader.getValue(f, false);

        // Nook Color
        f = new File("/sys/class/power_supply/max17042-0/current_now");
        if (f.exists())
            return OneLineReader.getValue(f, false);

        // Xperia Arc
        f = new File("/sys/class/power_supply/bq27520/current_now");
        if (f.exists())
            return OneLineReader.getValue(f, true);

        // Motorola Atrix
        f = new File(
                "/sys/devices/platform/cpcap_battery/power_supply/usb/current_now");
        if (f.exists())
            return OneLineReader.getValue(f, false);

        // Acer Iconia Tab A500
        f = new File("/sys/EcControl/BatCurrent");
        if (f.exists())
            return OneLineReader.getValue(f, false);

        // charge current only, Samsung Note
        f = new File("/sys/class/power_supply/battery/batt_current_now");
        if (f.exists())
            return OneLineReader.getValue(f, false);

        // galaxy note, galaxy s2
        f = new File("/sys/class/power_supply/battery/batt_current_adc");
        if (f.exists())
            return OneLineReader.getValue(f, false);

        // intel
        f = new File("/sys/class/power_supply/max170xx_battery/current_now");
        if (f.exists())
            return OneLineReader.getValue(f, true);

        // Sony Xperia U
        f = new File("/sys/class/power_supply/ab8500_fg/current_now");
        if (f.exists())
            return OneLineReader.getValue(f, true);

        f = new File("/sys/class/power_supply/android-battery/current_now");
        if (f.exists()) {
            return OneLineReader.getValue(f, false);
        }

        // Nexus 10, 4.4.
        f = new File("/sys/class/power_supply/ds2784-fuelgauge/current_now");
        if (f.exists()) {
            return OneLineReader.getValue(f, true);
        }

        f = new File("/sys/class/power_supply/Battery/current_now");
        if (f.exists()) {
            return OneLineReader.getValue(f, false);
        }

        return null;
    }

    static private class OneLineReader {
        static public Long getValue(File f, boolean convertToMillis) {

            String text = null;

            try {
                FileInputStream fs = new FileInputStream(f);
                InputStreamReader sr = new InputStreamReader(fs);
                BufferedReader br = new BufferedReader(sr);

                text = br.readLine();

                br.close();
                sr.close();
                fs.close();
            } catch (Exception ex) {
                Log.e("CurrentWidget", ex.getMessage());
                ex.printStackTrace();
            }

            Long value = null;

            if (text != null) {
                try	{
                    value = Long.parseLong(text);
                } catch (NumberFormatException nfe) 	{
                    Log.e("CurrentWidget", nfe.getMessage());
                    value = null;
                }

                if (convertToMillis && value != null) {
                    value = value / 1000; // convert to milliampere
                }
            }

            return value;
        }
    }
}