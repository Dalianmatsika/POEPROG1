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
        int count = 1;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                bedLayout[r][c] = String.format("B%02d", count++);
            }
        }
    }

    // Fixed: Added missing findBedPosition helper method
    private int[] findBedPosition(String bedNumber) {
        int count = 1;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                String id = String.format("B%02d", count++);
                if (id.equalsIgnoreCase(bedNumber)) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    public boolean registerPatient(Patient p) {
        for (Patient existing : patients) {
            if (existing.getPatientId().equalsIgnoreCase(p.getPatientId())) {
                throw new IllegalArgumentException("Patient ID already exists.");
            }
        }
        return patients.add(p);
    }

    public Patient searchPatient(String patientId) {
        for (Patient p : patients) {
            if (p.getPatientId().equalsIgnoreCase(patientId)) {
                return p;
            }
        }
        return null;
    }

    public boolean updatePatient(String id, String firstName, String lastName, int age, String condition) {
        Patient p = searchPatient(id);
        if (p != null) {
            p.setFirstName(firstName);
            p.setLastName(lastName);
            p.setAge(age);
            p.setMedicalCondition(condition);
            return true;
        }
        return false;
    }

    public boolean deletePatient(String id) {
        Patient p = searchPatient(id);
        if (p != null) {
            if (p instanceof Inpatient) {
                releaseBed(((Inpatient) p).getBedNumber());
            }
            return patients.remove(p);
        }
        return false;
    }

    public boolean allocateBed(String patientId, String bedNumber) {
        Patient p = searchPatient(patientId);
        if (p == null) throw new IllegalArgumentException("Patient not found.");
        if (p.getCategory() != PatientCategory.INPATIENT) {
            throw new IllegalArgumentException("Only inpatients can be allocated a bed.");
        }
        if (getOccupiedBedsCount() >= 20) {
            throw new IllegalStateException("Ward is fully occupied.");
        }

        int[] pos = findBedPosition(bedNumber);
        if (pos == null) throw new IllegalArgumentException("Invalid bed ID.");
        if (bedLayout[pos[0]][pos[1]].equals("[X]")) {
            throw new IllegalStateException("Bed is already occupied.");
        }

        bedLayout[pos[0]][pos[1]] = "[X]";
        if (!(p instanceof Inpatient)) {
            patients.remove(p);
            Inpatient inpatient = new Inpatient(p.getPatientId(), p.getFirstName(), p.getLastName(), p.getAge(), p.getGender(), p.getMedicalCondition(), wardNumber, bedNumber);
            patients.add(inpatient);
        } else {
            ((Inpatient) p).setBedNumber(bedNumber);
        }
        return true;
    }

    public boolean releaseBed(String bedNumber) {
        int count = 1;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                String id = String.format("B%02d", count++);
                if (id.equalsIgnoreCase(bedNumber)) {
                    if (bedLayout[r][c].equals("[X]")) {
                        bedLayout[r][c] = id;
                        for (Patient p : patients) {
                            if (p instanceof Inpatient && ((Inpatient) p).getBedNumber().equalsIgnoreCase(bedNumber)) {
                                ((Inpatient) p).setBedNumber("None");
                            }
                        }
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    public void displayWardLayout() {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                System.out.print(bedLayout[r][c] + "\t");
            }
            System.out.println();
        }
    }

    public List<String> getAvailableBeds() {
        List<String> list = new ArrayList<>();
        int count = 1;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                String id = String.format("B%02d", count++);
                if (!bedLayout[r][c].equals("[X]")) list.add(id);
            }
        }
        return list;
    }

    public List<String> getOccupiedBeds() {
        List<String> list = new ArrayList<>();
        int count = 1;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                String id = String.format("B%02d", count++);
                if (bedLayout[r][c].equals("[X]")) list.add(id);
            }
        }
        return list;
    }

    public int getOccupiedBedsCount() { return getOccupiedBeds().size(); }
    public int getTotalPatients() { return patients.size(); }
    public double getOccupancyPercentage() { return (getOccupiedBedsCount() / 20.0) * 100; }

    public void sortPatientsById() {
        patients.sort(Comparator.comparing(Patient::getPatientId));
    }

    public void sortPatientsByLastName() {
        patients.sort(Comparator.comparing(Patient::getLastName));
    }

    public List<Patient> getPatients() { return Collections.unmodifiableList(patients); }
}