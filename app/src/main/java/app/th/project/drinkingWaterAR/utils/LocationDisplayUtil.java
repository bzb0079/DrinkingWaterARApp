package app.th.project.drinkingWaterAR.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public final class LocationDisplayUtil {

        private static final int MIN_DISTANCE = 2;
        private static final int MAX_DISTANCE = 7000;
        public static final float INVALID_SCALE_MODIFIER = -1.0F;
        public static final float SCALE_MODIFIER = 0.5F;


//        @Nullable
        public final static Session setupSession(@NotNull Activity activity, boolean installRequested) throws UnavailableException {
            Session session = new Session(activity);
            Config config = new Config(session);
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            session.configure(config);
            return session;
        }

        public final static void handleSessionException(@NotNull Activity activity, @NotNull UnavailableException sessionException) {
            String message = sessionException instanceof UnavailableArcoreNotInstalledException ? "Ar core is not installed" : (sessionException instanceof UnavailableUserDeclinedInstallationException ? "Please install ARCore" : (sessionException instanceof UnavailableApkTooOldException ? "Please update ARCOre" : (sessionException instanceof UnavailableSdkTooOldException ? "This device doesnot support ARCore" : "This device doesnot support ARCore")));
            Toast.makeText((Context)activity, (CharSequence)message, Toast.LENGTH_LONG).show();
        }

        public static final float distanceBasedModifier(int distance) {
            float scaleModifier;
            if (Integer.MIN_VALUE <= distance) {
                if (2 >= distance) {
                    scaleModifier = -1.0F;
                    return scaleModifier;
                }
            }

            if (3 <= distance) {
                if (20 >= distance) {
                    scaleModifier = 0.8F;
                    return scaleModifier;
                }
            }

            if (21 <= distance) {
                if (40 >= distance) {
                    scaleModifier = 0.75F;
                    return scaleModifier;
                }
            }

            if (41 <= distance) {
                if (60 >= distance) {
                    scaleModifier = 0.7F;
                    return scaleModifier;
                }
            }

            if (61 <= distance) {
                if (80 >= distance) {
                    scaleModifier = 0.65F;
                    return scaleModifier;
                }
            }

            if (81 <= distance) {
                if (100 >= distance) {
                    scaleModifier = 0.6F;
                    return scaleModifier;
                }
            }

            if (101 <= distance) {
                if (1000 >= distance) {
                    scaleModifier = 0.5F;
                    return scaleModifier;
                }
            }

            if (1001 <= distance) {
                if (1500 >= distance) {
                    scaleModifier = 0.45F;
                    return scaleModifier;
                }
            }

            if (1501 <= distance) {
                if (2000 >= distance) {
                    scaleModifier = 0.4F;
                    return scaleModifier;
                }
            }

            if (2001 <= distance) {
                if (2500 >= distance) {
                    scaleModifier = 0.35F;
                    return scaleModifier;
                }
            }

            if (2501 <= distance) {
                if (3000 >= distance) {
                    scaleModifier = 0.3F;
                    return scaleModifier;
                }
            }

            if (3001 <= distance) {
                if (7000 >= distance) {
                    scaleModifier = 0.25F;
                    return scaleModifier;
                }
            }

            if (7001 <= distance) {
                if (Integer.MAX_VALUE >= distance) {
                    scaleModifier = 0.15F;
                    return scaleModifier;
                }
            }

            scaleModifier = -1.0F;
            return scaleModifier;
        }

        public final static float createHeight(int distance) {
            float randomHeight = 0.0F;
            if (0 <= distance) {
                if (1000 >= distance) {
                    return (float) (Math.random() * (3.0f - 1.0f)) + 1.0f;
                }
            }

            if (1001 <= distance) {
                if (1500 >= distance) {
                    return (float) (Math.random() * (6.0f - 4.0f)) + 4.0f;
                }
            }

            if (1501 <= distance) {
                if (2000 >= distance) {
                    return (float) (Math.random() * (9.0f - 7.0f)) + 7.0f;
                }
            }

            if (2001 <= distance) {
                if (3000 >= distance) {

                    return (float) (Math.random() * (12.0f - 10.0f)) + 10.0f;
                }
            }

            if (3001 <= distance) {
                if (7000 >= distance) {

                    return (float) (Math.random() * (13.0f - 12.0f)) + 12.0f;
                }
            }
            return randomHeight;
        }

        @NotNull
        public final static String showDistance(int distance) {
            StringBuilder stringBuilder = new StringBuilder();
            DecimalFormat df = new DecimalFormat("#.##");
            if (distance >= 1000) {
                double distanceDouble = Double.valueOf(df.format((double) distance / 1609.344));
                stringBuilder.append(distanceDouble).append("mi");
//                stringBuilder.append((double) distance / (double) 1000).append(" km");
            } else {
                stringBuilder.append(distance).append(" m");
            }
            return stringBuilder.toString();
        }
    }
