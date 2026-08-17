import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WardManagerTest {
    private WardManager manager;

    @BeforeEach
    public void setUp() {
        manager = new WardManager();
    }

    @Test
    public void testRegisterPatient() {
        Patient p = new Patient("P01", "John", "Doe", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        assertTrue(manager.registerPatient(p));
        assertEquals(1, manager.getTotalPatients());
    }

    @Test
    public void testSearchPatient() {
        Patient p = new Patient("P01", "John", "Doe", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        manager.registerPatient(p);
        assertNotNull(manager.searchPatient("P01"));
        assertNull(manager.searchPatient("P99"));
    }

    @Test
    public void testUpdatePatient() {
        Patient p = new Patient("P01", "John", "Doe", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        manager.registerPatient(p);
        assertTrue(manager.updatePatient("P01", "Johnny", "Doe", 31, "Recovered"));
        assertEquals("Johnny", manager.searchPatient("P01").getFirstName());
    }

    @Test
    public void testDeletePatient() {
        Patient p = new Patient("P01", "John", "Doe", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        manager.registerPatient(p);
        assertTrue(manager.deletePatient("P01"));
        assertEquals(0, manager.getTotalPatients());
    }

    @Test
    public void testAllocateBed() {
        Inpatient p = new Inpatient("P01", "Jane", "Smith", 25, "Female", "Surgery", "W1", "Unassigned");
        manager.registerPatient(p);
        assertTrue(manager.allocateBed("P01", "B01"));
        assertEquals(1, manager.getOccupiedBedsCount());
    }

    @Test
    public void testReleaseBed() {
        Inpatient p = new Inpatient("P01", "Jane", "Smith", 25, "Female", "Surgery", "W1", "Unassigned");
        manager.registerPatient(p);
        manager.allocateBed("P01", "B01");
        assertTrue(manager.releaseBed("B01"));
        assertEquals(0, manager.getOccupiedBedsCount());
    }

    @Test
    public void testPreventDuplicatePatientIds() {
        Patient p1 = new Patient("P01", "John", "Doe", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        Patient p2 = new Patient("P01", "Jane", "Doe", 28, "Female", "Cold", PatientCategory.OUTPATIENT);
        manager.registerPatient(p1);
        assertThrows(IllegalArgumentException.class, () -> manager.registerPatient(p2));
    }

    @Test
    public void testPreventAllocatingOccupiedBed() {
        Inpatient p1 = new Inpatient("P01", "John", "Doe", 30, "Male", "Flu", "W1", "Unassigned");
        Inpatient p2 = new Inpatient("P02", "Jane", "Doe", 28, "Female", "Cold", "W1", "Unassigned");
        manager.registerPatient(p1);
        manager.registerPatient(p2);
        manager.allocateBed("P01", "B01");
        assertThrows(IllegalStateException.class, () -> manager.allocateBed("P02", "B01"));
    }

    @Test
    public void testPreventBedAllocationWhenFull() {
        for (int i = 1; i <= 20; i++) {
            String id = String.format("P%02d", i);
            String bed = String.format("B%02d", i);
            Inpatient p = new Inpatient(id, "Test", "User", 20, "Male", "None", "W1", "Unassigned");
            manager.registerPatient(p);
            manager.allocateBed(id, bed);
        }
        Inpatient extra = new Inpatient("P21", "Extra", "User", 20, "Male", "None", "W1", "Unassigned");
        manager.registerPatient(extra);
        assertThrows(IllegalStateException.class, () -> manager.allocateBed("P21", "B01"));
    }

    @Test
    public void testSortPatientsBySurnameAndId() {
        Patient p1 = new Patient("P02", "B", "Smith", 30, "M", "None", PatientCategory.OUTPATIENT);
        Patient p2 = new Patient("P01", "A", "Adams", 25, "F", "None", PatientCategory.OUTPATIENT);
        manager.registerPatient(p1);
        manager.registerPatient(p2);

        manager.sortPatientsByLastName();
        assertEquals("Adams", manager.getPatients().get(0).getLastName());

        manager.sortPatientsById();
        assertEquals("P01", manager.getPatients().get(0).getPatientId());
    }
}