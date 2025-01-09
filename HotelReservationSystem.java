import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Scanner;

public class HotelReservationSystem {

	private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
	private static final String username = "root";
	private static final String password = "Tanvir@9110";

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try {
			Connection connection = DriverManager.getConnection(url, username, password);
			while (true) {
				System.out.println();
				System.out.println("HOTEL MANAGEMENT SYSTEM");
				Scanner scanner = new Scanner(System.in);
				System.out.println("1. Reserve a room");
				System.out.println("2. View Reservations");
				System.out.println("3. Get Room Number");
				System.out.println("4. Update Reservations");
				System.out.println("5. Delete Reservations");
				System.out.println("0. Exit");
				System.out.print("Choose an option: ");
				int choice = scanner.nextInt();
				switch (choice) {
				case 1:
					reserveRoom(connection, scanner);
					break;
				case 2:
					viewReservations(connection);
					break;
				case 3:
					getRoomNumber(connection, scanner);
					break;
				case 4:
					updateReservation(connection, scanner);
					break;
				case 5:
					deleteReservation(connection, scanner);
					break;

				case 0:
					exit();
					scanner.close();
					return;
				default:
					System.out.println("Invalid choice. Try again.");
				}
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static void reserveRoom(Connection connection, Scanner scanner) {
		try {
			System.out.print("Enter guest name: ");
			String guestName = scanner.next();
			scanner.nextLine();
			System.out.print("Enter room number: ");
			int roomNumber = scanner.nextInt();
			System.out.print("Enter contact number: ");
			String contactNumber = scanner.next();

			String sql = "INSERT INTO reservations (guestName, roomNumber, contactNumber) " + "VALUES ('" + guestName
					+ "', " + roomNumber + ", '" + contactNumber + "')";

			System.out.println(sql);
			try (Statement statement = connection.createStatement()) {
				int affectedRows = statement.executeUpdate(sql);

				if (affectedRows > 0) {
					System.out.println("Reservation successful!");
				} else {
					System.out.println("Reservation failed.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void viewReservations(Connection connection) throws SQLException {
		String sql = "SELECT id, guestName,roomNumber,contactNumber,reservationDate FROM reservations";
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {

			System.out.println("Current Reservation");
			System.out.println("+---------------+------------+-------------+----------------+----------------+");
			System.out.println("|Reservation ID | Guest Name | Room Number | Contact Number | Reservation Date |");
			System.out.println("+---------------+------------+-------------+----------------+----------------+");

			while (resultSet.next()) {
				int reservationId = resultSet.getInt("id");
				String guestName = resultSet.getString("guestName");
				int roomNumber = resultSet.getInt("roomNumber");
				String contactNumber = resultSet.getString("contactNumber");
				String reservationDate = resultSet.getTimestamp("reservationDate").toString();

//				System.out.printf("Format and display the reservation data", reservationId, guestName, roomNumber,
//						contactNumber, reservationDate);
				System.out.println("reservationId " + reservationId + ", GuestName: " + guestName + ", Contact Number: "
						+ contactNumber + ", Reservation Date: " + reservationDate);
			}
			System.out.println("+----------+------------+-----------+--------+------------+");
		}
	}

	private static void getRoomNumber(Connection connection, Scanner scanner) {
		try {
			System.out.println("Enter reservation ID: ");
			int reservationId = scanner.nextInt();
			String sql = "SELECT roomNumber,guestName FROM hotel_db.reservations" + " WHERE id = " + reservationId;
			System.out.println(sql);

			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(sql)) {

				if (resultSet.next()) {

					int roomNumber = resultSet.getInt("roomNumber");
					String guestName = resultSet.getString("guestName");
					System.out.println("Room number for reservation ID : " + reservationId + " roomNumber is:  " + roomNumber+" GuestName is: "+guestName);

				} else {
					System.out.println(
							"Reservation is not found please book your reservation get reservationId and guestName :");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

	private static void updateReservation(Connection connection, Scanner scanner) {
		try {
			System.out.println("Enter the reservation ID to Update: ");
			int reservationId = scanner.nextInt();
			scanner.nextLine();

			if (!reservationExists(connection, reservationId)) {
				System.out.println("Reservation not found for the given Id ");
				return;
			}

			System.out.println("Enter new guest name: ");
			String newGuestName = scanner.nextLine();
			System.out.println("Enter new room number: ");
			int newRoomNumber = scanner.nextInt();
			System.out.println("Enter the contact number: ");
			String newContactNumber = scanner.next();

			String sql = "UPDATE hotel_db.reservations SET "
					+"guestName="+"'"+newGuestName+"'"
					+", roomNumber="+"'"+newRoomNumber+"'"
					+", contactNumber="+"'"+newContactNumber+"'"
					+" WHERE id="+reservationId;
			try (Statement statement = connection.createStatement()) {
				int affectedRows = statement.executeUpdate(sql);

				if (affectedRows > 0) {
					System.out.println("Reservation updated successfully!");
				} else {
					System.out.println("Reservation update failed:");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void deleteReservation(Connection connection, Scanner scanner) {
		try {
			System.out.println("Enter the reservation id to delete:");
			int reservationId = scanner.nextInt();

			if (!reservationExists(connection, reservationId)) {
				return;
			}
			String sql = "DELETE FROM hotel_db.reservations WHERE reservationId = " + reservationId;
			System.out.println(sql
					);
			try (Statement statement = connection.createStatement()) {
				int affectedRows = statement.executeUpdate(sql);

				if (affectedRows > 0) {
					System.out.println("Reservation deleted successfully: ");
				} else {
					System.out.println("Reservation deletion failed: ");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static boolean reservationExists(Connection connection, int reservationId) {
		try {
			String sql = "SELECT id FROM reservations WHERE id = " + reservationId;

			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(sql)) {

				return resultSet.next(); //
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static void exit() throws InterruptedException {
		System.out.print("Exiting System:");
		int i = 5;
		while (i != 0) {
			System.out.print("-");
			Thread.sleep(400);
			i--;
		}
		System.out.println();
		System.out.println("<<<<<<<<<<<<<<<<<<ThankYou For Using Hotel Reservation System!!!>>>>>>>>>>>>>>>>>>>");
	}
}
