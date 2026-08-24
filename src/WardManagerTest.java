import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WardManagerTest {
    private WardManager manager;

    @BeforeEach
    public void setUp() {
        manager = new WardManager();
    }

    @Test
    public void testRegisterPatient() {
        Patient p = new Patient("P001", "John", "Doe", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        assertTrue(manager.registerPatient(p));
        assertEquals(1, manager.getAllPatients().size());
    }

    @Test
    public void testPreventDuplicatePatientId() {
        Patient p1 = new Patient("P001", "John", "Doe", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        Patient p2 = new Patient("P001", "Jane", "Smith", 25, "Female", "Fever", PatientCategory.EMERGENCY);
        assertTrue(manager.registerPatient(p1));
        assertFalse(manager.registerPatient(p2));
    }

    @Test
    public void testSearchPatient() {
        Patient p = new Patient("P001", "John", "Doe", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        manager.registerPatient(p);
        assertNotNull(manager.findPatientById("P001"));
        assertNull(manager.findPatientById("P999"));
    }

    @Test
    public void testUpdatePatientDetails() {
        Patient p = new Patient("P001", "John", "Doe", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        manager.registerPatient(p);
        assertTrue(manager.updatePatient("P001", "Johnathan", "Doe", 31, "Male", "Recovered"));
        assertEquals("Johnathan", manager.findPatientById("P001").getFirstName());
    }

    @Test
    public void testDeletePatient() {
        Patient p = new Patient("P001", "John", "Doe", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        manager.registerPatient(p);
        assertTrue(manager.deletePatient("P001"));
        assertNull(manager.findPatientById("P001"));
    }

    @Test
    public void testAllocateBed() {
        Patient p = new Inpatient("P001", "John", "Doe", 30, "Male", "Surgery", "W1", "None");
        manager.registerPatient(p);
        assertTrue(manager.allocateBed("P001", "B01"));
        assertTrue(manager.isBedOccupied("B01"));
    }

    @Test
    public void testPreventAllocatingOccupiedBed() {
        Patient p1 = new Inpatient("P001", "John", "Doe", 30, "Male", "Surgery", "W1", "None");
        Patient p2 = new Inpatient("P002", "Jane", "Smith", 25, "Female", "Observation", "W1", "None");
        manager.registerPatient(p1);
        manager.registerPatient(p2);

        assertTrue(manager.allocateBed("P001", "B01"));
        assertFalse(manager.allocateBed("P002", "B01"));
    }

    @Test
    public void testReleaseBed() {
        Patient p = new Inpatient("P001", "John", "Doe", 30, "Male", "Surgery", "W1", "None");
        manager.registerPatient(p);
        manager.allocateBed("P001", "B01");
        assertTrue(manager.releaseBed("B01"));
        assertFalse(manager.isBedOccupied("B01"));
    }

    @Test
    public void testPreventAllocationWhenFull() {
        for (int i = 1; i <= 20; i++) {
            String id = "P" + String.format("%03d", i);
            String bed = String.format("B%02d", i);
            Patient p = new Inpatient(id, "Test", "User", 20, "Male", "Condition", "W1", "None");
            manager.registerPatient(p);
            manager.allocateBed(id, bed);
        }

        Patient extra = new Inpatient("P021", "Extra", "User", 20, "Male", "Condition", "W1", "None");
        manager.registerPatient(extra);
        assertFalse(manager.allocateBed("P021", "B01"));
    }

    @Test
    public void testSortingPatients() {
        Patient p1 = new Patient("P002", "B", "Zack", 30, "M", "A", PatientCategory.OUTPATIENT);
        Patient p2 = new Patient("P001", "A", "Adams", 25, "F", "B", PatientCategory.EMERGENCY);
        manager.registerPatient(p1);
        manager.registerPatient(p2);

        List<Patient> sortedBySurname = manager.getPatientsSortedBySurname();
        assertEquals("Adams", sortedBySurname.get(0).getLastName());

        List<Patient> sortedById = manager.getPatientsSortedById();
        assertEquals("P001", sortedById.get(0).getPatientId());
    }
}