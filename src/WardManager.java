import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WardManager {
    private final List<Patient> patients = new ArrayList<>();
    private final String[][] bedLayout = new String[4][5];
    private final String wardNumber = "W1";

    public WardManager() {
        initializeBeds();
    }

    private void initializeBeds() {
        int bedNum = 1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                bedLayout[i][j] = String.format("B%02d", bedNum++);
            }
        }
    }

    public boolean registerPatient(Patient patient) {
        if (findPatientById(patient.getPatientId()) != null) {
            return false; // Duplicate Patient ID
        }
        patients.add(patient);
        return true;
    }

    public Patient findPatientById(String id) {
        for (Patient p : patients) {
            if (p.getPatientId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null;
    }

    public boolean updatePatient(String id, String firstName, String lastName, int age, String gender, String condition) {
        Patient p = findPatientById(id);
        if (p != null) {
            p.setFirstName(firstName);
            p.setLastName(lastName);
            p.setAge(age);
            p.setGender(gender);
            p.setMedicalCondition(condition);
            return true;
        }
        return false;
    }

    public boolean deletePatient(String id) {
        Patient p = findPatientById(id);
        if (p != null) {
            if (p instanceof Inpatient inpatient) {
                releaseBed(inpatient.getBedNumber());
            }
            patients.remove(p);
            return true;
        }
        return false;
    }

    public boolean isBedOccupied(String bedCode) {
        for (Patient p : patients) {
            if (p instanceof Inpatient inp && inp.getBedNumber().equalsIgnoreCase(bedCode)) {
                return true;
            }
        }
        return false;
    }

    public boolean isBedValid(String bedCode) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                if (bedLayout[i][j].equalsIgnoreCase(bedCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean allocateBed(String patientId, String bedCode) {
        Patient p = findPatientById(patientId);
        if (p == null || !isBedValid(bedCode) || isBedOccupied(bedCode) || getOccupiedBedCount() >= 20) {
            return false;
        }

        if (p.getCategory() == PatientCategory.INPATIENT) {
            ((Inpatient) p).setBedNumber(bedCode.toUpperCase());
            ((Inpatient) p).setWardNumber(wardNumber);
        } else {
            // Convert existing patient to Inpatient
            Inpatient inp = new Inpatient(p.getPatientId(), p.getFirstName(), p.getLastName(),
                    p.getAge(), p.getGender(), p.getMedicalCondition(), wardNumber, bedCode.toUpperCase());
            patients.remove(p);
            patients.add(inp);
        }
        return true;
    }

    public boolean releaseBed(String bedCode) {
        for (Patient p : patients) {
            if (p instanceof Inpatient inp && inp.getBedNumber().equalsIgnoreCase(bedCode)) {
                inp.setBedNumber("None");
                inp.setWardNumber("N/A");
                return true;
            }
        }
        return false;
    }

    public void displayWardLayout() {
        System.out.println("\n=== Ward Bed Layout (4x5) ===");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                String bed = bedLayout[i][j];
                if (isBedOccupied(bed)) {
                    System.out.printf("[%s:OCC] ", bed);
                } else {
                    System.out.printf("[%s:AVL] ", bed);
                }
            }
            System.out.println();
        }
    }

    public List<String> getAvailableBeds() {
        List<String> available = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                String bed = bedLayout[i][j];
                if (!isBedOccupied(bed)) {
                    available.add(bed);
                }
            }
        }
        return available;
    }

    public List<String> getOccupiedBeds() {
        List<String> occupied = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                String bed = bedLayout[i][j];
                if (isBedOccupied(bed)) {
                    occupied.add(bed);
                }
            }
        }
        return occupied;
    }

    public int getOccupiedBedCount() {
        return getOccupiedBeds().size();
    }

    public double getOccupancyPercentage() {
        return (getOccupiedBedCount() / 20.0) * 100.0;
    }

    public List<Patient> getPatientsSortedBySurname() {
        List<Patient> sorted = new ArrayList<>(patients);
        sorted.sort(Comparator.comparing(Patient::getLastName, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    public List<Patient> getPatientsSortedById() {
        List<Patient> sorted = new ArrayList<>(patients);
        sorted.sort(Comparator.comparing(Patient::getPatientId));
        return sorted;
    }

    public List<Patient> getAllPatients() {
        return patients;
    }
}