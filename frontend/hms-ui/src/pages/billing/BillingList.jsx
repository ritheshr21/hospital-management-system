import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AppLayout from "../../components/AppLayout";
import { listBills, cancelBill } from "../../api/billing";
import "../../styles/patients.css";
import "../../styles/billing.css";

function BillingList() {
  const navigate = useNavigate();
  const [bills, setBills] = useState([]);
  const [status, setStatus] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const load = async (s) => {
    setLoading(true);
    setError("");
    try {
      setBills(await listBills(s || undefined));
    } catch (e) {
      setError(e.response?.data?.message || "Failed to load bills");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load("");
  }, []);

  const handleCancel = async (bill) => {
    if (!window.confirm(`Cancel invoice ${bill.invoiceNumber}?`)) return;
    try {
      await cancelBill(bill.id);
      load(status);
    } catch (e) {
      setError(e.response?.data?.message || "Failed to cancel bill");
    }
  };

  const money = (v) => (v == null ? "-" : `₹${Number(v).toFixed(2)}`);

  return (
    <AppLayout title="Billing">
      <div className="toolbar">
        <select
          value={status}
          onChange={(e) => { setStatus(e.target.value); load(e.target.value); }}
          style={{ padding: "12px 14px", borderRadius: 8, border: "1px solid var(--hms-border)" }}
        >
          <option value="">All bills</option>
          <option value="PENDING">Pending</option>
          <option value="PAID">Paid</option>
          <option value="CANCELLED">Cancelled</option>
        </select>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <div className="alert-empty">Loading...</div>
      ) : bills.length === 0 ? (
        <div className="alert-empty">
          No bills yet. Bills are raised automatically when an appointment is completed.
        </div>
      ) : (
        <div className="table-wrap">
          <table className="patient-table">
            <thead>
              <tr>
                <th>Invoice</th>
                <th>Patient</th>
                <th>Department</th>
                <th className="num">Total</th>
                <th>Status</th>
                <th>Method</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {bills.map((b) => (
                <tr key={b.id}>
                  <td>{b.invoiceNumber}</td>
                  <td>{b.patientName}</td>
                  <td>{b.department || "-"}</td>
                  <td className="amount">{money(b.total)}</td>
                  <td><span className={`bill-status bill-${b.status}`}>{b.status}</span></td>
                  <td>{b.paymentMethod || "-"}</td>
                  <td>
                    <div className="row-actions">
                      <button className="btn btn-ghost btn-sm" onClick={() => navigate(`/bills/${b.id}`)}>
                        {b.status === "PENDING" ? "Pay" : "Invoice"}
                      </button>
                      {b.status === "PENDING" && (
                        <button className="btn btn-danger btn-sm" onClick={() => handleCancel(b)}>Cancel</button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </AppLayout>
  );
}

export default BillingList;
