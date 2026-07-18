import { BrowserRouter, Route, Routes } from "react-router-dom";
import Dashboard from "./pages/Dashboard";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Unauthorized from "./pages/Unauthorized";
import PatientList from "./pages/patients/PatientList";
import PatientForm from "./pages/patients/PatientForm";
import PatientProfile from "./pages/patients/PatientProfile";
import DepartmentsPage from "./pages/departments/DepartmentsPage";
import DoctorList from "./pages/doctors/DoctorList";
import DoctorForm from "./pages/doctors/DoctorForm";
import DoctorProfile from "./pages/doctors/DoctorProfile";
import ProtectedRoute from "./routes/ProtectedRoute";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/unauthorized" element={<Unauthorized />} />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path="/patients"
          element={
            <ProtectedRoute>
              <PatientList />
            </ProtectedRoute>
          }
        />
        <Route
          path="/patients/new"
          element={
            <ProtectedRoute>
              <PatientForm />
            </ProtectedRoute>
          }
        />
        <Route
          path="/patients/:id"
          element={
            <ProtectedRoute>
              <PatientProfile />
            </ProtectedRoute>
          }
        />
        <Route
          path="/patients/:id/edit"
          element={
            <ProtectedRoute>
              <PatientForm />
            </ProtectedRoute>
          }
        />

        <Route
          path="/departments"
          element={
            <ProtectedRoute>
              <DepartmentsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/doctors"
          element={
            <ProtectedRoute>
              <DoctorList />
            </ProtectedRoute>
          }
        />
        <Route
          path="/doctors/new"
          element={
            <ProtectedRoute>
              <DoctorForm />
            </ProtectedRoute>
          }
        />
        <Route
          path="/doctors/:id"
          element={
            <ProtectedRoute>
              <DoctorProfile />
            </ProtectedRoute>
          }
        />
        <Route
          path="/doctors/:id/edit"
          element={
            <ProtectedRoute>
              <DoctorForm />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
