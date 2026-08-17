public enum PatientCategory {
    INPATIENT, OUTPATIENT, EMERGENCY;

    public static PatientCategory fromString(String category) {
        for (PatientCategory c : PatientCategory.values()) {
            if (c.name().equalsIgnoreCase(category)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Invalid category: " + category);
    }
}