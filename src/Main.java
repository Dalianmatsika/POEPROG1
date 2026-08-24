import java.util.Scanner;

public class Main {
    private static final WardManager manager = new WardManager();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {

            //Main Menu loop control section
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
                    case 2 -> searchPatient();
                    case 3 -> updatePatient();
                    case 4 -> deletePatient();
                    case 5 -> allocateBed();
                    case 6 -> releaseBed();
                    case 7 -> manager.displayWardLayout();
                    case 8 -> generateReports();
                    case 9 -> { System.out.println("Exiting system..."); return; }
                    default -> System.out.println("Invalid selection.Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
      //Asking the user to input information required for registering a new patient
    private static void registerPatientUI() {
        System.out.print("Enter Patient ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter First Name: ");
        String fn = scanner.nextLine();

        System.out.print("Enter Last Name: ");
        String ln = scanner.nextLine();

        System.out.print("Enter Age: ");
        int age = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Gender: ");
        String g = scanner.nextLine();

        System.out.print("Enter Medical Condition: ");
        String cond = scanner.nextLine();

        System.out.print("Select Category (1.INPATIENT, 2.OUTPATIENT, 3.EMERGENCY): ");
        String catChoice = (scanner.nextLine()).trim();

        Patient patient;
        if ("1".equals(catChoice)) {
            patient = new Inpatient(id, fn, ln, age, g, cond, "W1", "None");
        } else if ("2".equals(catChoice)) {
            patient = new Patient(id, fn, ln, age, g, cond, PatientCategory.OUTPATIENT);
        } else {
            patient = new Patient(id, fn, ln, age, g, cond,PatientCategory.EMERGENCY);
        }

        if (manager.registerPatient(patient)) {
            System.out.println("Patient registered successfully!");
        } else {
            System.out.println("Error: Duplicate Patient ID.");
        }
    }
    //Search for patient
    private static void searchPatient() {
        System.out.print("Enter Patient ID to search: ");
        String id = scanner.nextLine().trim();
        Patient p = manager.findPatientById(id);
        if (p != null) {
            System.out.println(p.displayDetails());
        } else {
            System.out.println("Patient not found.");
        }
    }
     //Update patient information
    private static void updatePatient() {
        System.out.print("Enter Patient ID to update: ");
        String id = scanner.nextLine().trim();
        if (manager.findPatientById(id) == null) {
            System.out.println("Patient not found.");
            return;
        }
        System.out.print("Enter New First Name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Enter New Last Name: ");
        String lastName = scanner.nextLine().trim();
        System.out.print("Enter New Age: ");
        int age = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter New Gender: ");
        String gender = scanner.nextLine().trim();
        System.out.print("Enter New Condition: ");
        String condition = scanner.nextLine().trim();

        if (manager.updatePatient(id, firstName, lastName, age, gender, condition)) {
            System.out.println("Patient updated successfully.");
        }
    }

    private static void deletePatient() {
        System.out.print("Enter Patient ID to delete: ");
        String id = scanner.nextLine().trim();
        if (manager.deletePatient(id)) {
            System.out.println("Patient record deleted.");
        } else {
            System.out.println("Patient not found.");
        }
    }

    private static void allocateBed() {
        System.out.print("Enter Patient ID for bed allocation: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Bed Number (e.g., B01 to B20): ");
        String bedCode = scanner.nextLine().trim();

        if (manager.allocateBed(id, bedCode)) {
            System.out.println("Bed allocated successfully!");
        } else {
            System.out.println("Allocation failed. Ensure Patient ID exists, Bed Code is valid (B01-B20), and Bed is not occupied.");
        }
    }

    private static void releaseBed() {
        System.out.print("Enter Bed Code to release (e.g., B01): ");
        String bedCode = scanner.nextLine().trim();
        if (manager.releaseBed(bedCode)) {
            System.out.println("Bed released successfully.");
        } else {
            System.out.println("Bed release failed or bed was not occupied.");
        }
    }

    private static void viewBedStatus() {
        manager.displayWardLayout();
        System.out.println("Available Beds: " + manager.getAvailableBeds());
        System.out.println("Occupied Beds: " + manager.getOccupiedBeds());
    }

    private static void generateReports() {
        System.out.println("\n--- WARD REPORTS ---");
        System.out.println("Total Registered Patients: " + manager.getAllPatients().size());
        System.out.println("Total Occupied Beds: " + manager.getOccupiedBedCount());
        System.out.printf("Ward Occupancy Percentage: %.2f%%\n", manager.getOccupancyPercentage());

        System.out.println("\n1. All Patients Sorted by Surname:");
        for (Patient p : manager.getPatientsSortedBySurname()) {
            System.out.println(p.displayDetails());
        }

        System.out.println("\n2. All Patients Sorted by ID:");
        for (Patient p : manager.getPatientsSortedById()) {
            System.out.println(p.displayDetails());
        }
    }
}