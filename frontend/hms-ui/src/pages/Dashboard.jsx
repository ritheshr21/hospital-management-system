import { useNavigate } from "react-router-dom";
import "./dashboard.css";

// Each action maps to a label and (optionally) a route it navigates to.
// Routes that aren't built yet stay null and render as "coming soon".
const dashboardOptions = {
  ADMIN: [
    { label: "Manage Patients", to: "/patients" },
    { label: "Manage Doctors", to: "/doctors" },
    { label: "Manage Departments", to: "/departments" },
    { label: "View Reports", to: null }
  ],
  DOCTOR: [
    { label: "View Appointments", to: null },
    { label: "Manage Availability", to: "/doctors" },
    { label: "Write Prescriptions", to: null },
    { label: "View Patient History", to: "/patients" }
  ],
  PATIENT: [
    { label: "Book Appointment", to: null },
    { label: "View Medical History", to: null },
    { label: "View Prescriptions", to: null },
    { label: "Pay Bills", to: null }
  ],
  RECEPTIONIST: [
    { label: "Register Walk-in Patients", to: "/patients" },
    { label: "Manage Queue", to: null },
    { label: "Book Appointments", to: null },
    { label: "Billing Support", to: null }
  ]
};

function Dashboard() {
  const navigate = useNavigate();
  const role = localStorage.getItem("role");
  const name = localStorage.getItem("name");

  const handleLogout = () => {
    localStorage.clear();
    window.location.href = "/login";
  };

  const options = dashboardOptions[role] || [];

  return (
    <div className="dashboard-container">
      <nav className="dashboard-navbar">
        <div>
          <h1>Hospital Management System</h1>
          <p className="role-badge">{role}</p>
        </div>

        <button onClick={handleLogout}>Logout</button>
      </nav>

      <div className="dashboard-content">
        {role ? (
          <>
            <h2 className="welcome-text">Welcome back{name ? `, ${name}` : ""}</h2>

            <div className="dashboard-actions">
              {options.map((option) => (
                <div
                  key={option.label}
                  className="action-card"
                  onClick={() => option.to && navigate(option.to)}
                  style={{ opacity: option.to ? 1 : 0.55, cursor: option.to ? "pointer" : "default" }}
                >
                  {option.label}
                  {!option.to && <span style={{ display: "block", fontSize: 12, color: "#94a3b8", marginTop: 6 }}>Coming soon</span>}
                </div>
              ))}
            </div>
          </>
        ) : (
          <h2>No Role Found</h2>
        )}
      </div>
    </div>
  );
}

export default Dashboard;
