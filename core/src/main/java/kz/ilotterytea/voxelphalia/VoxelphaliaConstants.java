package kz.ilotterytea.voxelphalia;

import java.time.LocalDate;
import java.time.Month;

public class VoxelphaliaConstants {
    public static class Metadata {
        public static final String APP_NAME = "voxelphalia";
        public static final String APP_ID = "voxelphalia";
        public static final String APP_PACKAGE = "kz.ilotterytea.voxelphalia";
        public static final String APP_DEV = "ilotterytea";
        public static final String APP_VERSION = "0.7.1";
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
