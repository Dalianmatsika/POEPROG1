import java.util.Scanner;

public class Main {
    private static final WardManager manager = new WardManager();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== MEDICARE HOSPITAL PATIENT ADMISSION SYSTEM ===");
            System.out.println("1. Register Patient");
            System.out.println("2. Search Patient");
            System.out.println("3. Update Patient");
            System.out.println("4. Delete Patient");
            System.out.println("5. Allocate Bed");
            System.out.println("6. Release Bed");
            System.out.println("7. Display Ward Layout");
            System.out.println("8. Ward Reports");
            System.out.println("9. Exit");
            System.out.print("Select an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> registerPatientUI();
                    case 2 -> searchPatientUI();
                    case 3 -> updatePatientUI();
                    case 4 -> deletePatientUI();
                    case 5 -> allocateBedUI();
                    case 6 -> releaseBedUI();
                    case 7 -> manager.displayWardLayout();
                    case 8 -> displayReportsUI();
                    case 9 -> { System.out.println("Exiting system..."); return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void registerPatientUI() {
        System.out.print("ID: "); String id = scanner.nextLine();
        System.out.print("First Name: "); String fn = scanner.nextLine();
        System.out.print("Last Name: "); String ln = scanner.nextLine();
        System.out.print("Age: "); int age = Integer.parseInt(scanner.nextLine());
        System.out.print("Gender: "); String g = scanner.nextLine();
        System.out.print("Condition: "); String cond = scanner.nextLine();
        System.out.print("Category (INPATIENT, OUTPATIENT, EMERGENCY): ");
        PatientCategory cat = PatientCategory.fromString(scanner.nextLine());

        Patient p;
        if (cat == PatientCategory.INPATIENT) {
            p = new Inpatient(id, fn, ln, age, g, cond, "W1", "Unassigned");
        } else {
            p = new Patient(id, fn, ln, age, g, cond, cat);
        }
        manager.registerPatient(p);
        System.out.println("Patient registered successfully.");
    }

    private static void searchPatientUI() {
        System.out.print("Enter ID: ");
        Patient p = manager.searchPatient(scanner.nextLine());
        System.out.println(p != null ? p.displayDetails() : "Patient not found.");
    }

    private static void updatePatientUI() {
        System.out.print("Enter ID: "); String id = scanner.nextLine();
        System.out.print("New First Name: "); String fn = scanner.nextLine();
        System.out.print("New Last Name: "); String ln = scanner.nextLine();
        System.out.print("New Age: "); int age = Integer.parseInt(scanner.nextLine());
        System.out.print("New Condition: "); String cond = scanner.nextLine();
        if (manager.updatePatient(id, fn, ln, age, cond)) {
            System.out.println("Patient updated.");
        } else {
            System.out.println("Patient not found.");
        }
    }

    private static void deletePatientUI() {
        System.out.print("Enter ID: ");
        if (manager.deletePatient(scanner.nextLine())) {
            System.out.println("Patient removed.");
        } else {
            System.out.println("Patient not found.");
        }
    }

    private static void allocateBedUI() {
        System.out.print("Enter Patient ID: "); String id = scanner.nextLine();
        System.out.print("Enter Bed Number (e.g. B01): "); String bed = scanner.nextLine();
        manager.allocateBed(id, bed);
        System.out.println("Bed allocated.");
    }

    private static void releaseBedUI() {
        System.out.print("Enter Bed Number: ");
        if (manager.releaseBed(scanner.nextLine())) {
            System.out.println("Bed released.");
        } else {
            System.out.println("Bed is not occupied or invalid.");
        }
    }

    private static void displayReportsUI() {
        System.out.println("\n--- WARD REPORTS ---");
        System.out.println("Total Patients: " + manager.getTotalPatients());
        System.out.println("Occupied Beds: " + manager.getOccupiedBedsCount());
        System.out.println("Occupancy Rate: " + manager.getOccupancyPercentage() + "%");
        System.out.println("Available Beds: " + manager.getAvailableBeds());
        System.out.println("Occupied Beds: " + manager.getOccupiedBeds());
        System.out.println("\n--- Registered Patients ---");
        manager.sortPatientsById();
        for (Patient p : manager.getPatients()) {
            System.out.println(p.displayDetails());
        }
    }
}