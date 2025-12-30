package kz.ilotterytea.voxelphalia;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import kz.ilotterytea.voxelphalia.utils.OSUtils;

import java.time.LocalDate;
import java.time.Month;

public class VoxelphaliaConstants {
    public static class Metadata {
        public static final String APP_NAME = "Voxelphalia";
        public static final String APP_ID = "voxelphalia";
        public static final String APP_PACKAGE = "kz.ilotterytea.voxelphalia";
        public static final String APP_DEV = "ilotterytea";
        public static final String APP_VERSION = "0.8";
    }

    public static class Paths {
        public static final String LEVEL_DIRECTORY = OSUtils.getUserDataDirectory() + "/saves";
        public static final String SCREENSHOTS_DIRECTORY = OSUtils.getPicturesDirectory();
        public static final String REPORTS_DIRECTORY = OSUtils.getUserDataDirectory() + "/crashreports";

        static {
            String[] paths = new String[]{
                LEVEL_DIRECTORY,
                SCREENSHOTS_DIRECTORY,
                REPORTS_DIRECTORY
            };

            for (String path : paths) {
                FileHandle dir = Gdx.files.absolute(path);
                if (!dir.exists()) dir.mkdirs();
                else if (!dir.isDirectory()) throw new RuntimeException(dir.path() + " is not a directory!");
            }
        }
    }

    public static class Date {
        private static final LocalDate today = LocalDate.now();
        public static final boolean IS_XMAS;

        static {
            int year = today.getYear();
            LocalDate start = LocalDate.of(year, Month.DECEMBER, 27);
            LocalDate end = LocalDate.of(year, Month.JANUARY, 7);
            if (today.getMonth() == Month.JANUARY) {
                start = start.minusYears(1);
            } else {
                end = end.plusYears(1);
            }

            IS_XMAS = !today.isBefore(start) && !today.isAfter(end);
        }
    }
}
