import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import AppLayout from "../../components/AppLayout";
import { getBill, payBill } from "../../api/billing";
import "../../styles/patients.css";
import "../../styles/billing.css";

const METHODS = ["CASH", "CARD", "UPI", "INSURANCE"];
const money = (v) => (v == null ? "-" : `₹${Number(v).toFixed(2)}`);

function InvoicePage() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [bill, setBill] = useState(null);
  const [method, setMethod] = useState("CASH");
  const [fields, setFields] = useState({ cardLast4: "", upiId: "", policyNumber: "" });
  const [error, setError] = useState("");
  const [paying, setPaying] = useState(false);

  const load = () =>
    getBill(id)
      .then(setBill)
      .catch((e) => setError(e.response?.data?.message || "Failed to load invoice"));

  useEffect(() => {
    load();
  }, [id]);

  const handlePay = async (e) => {
    e.preventDefault();
    setError("");
    setPaying(true);
    try {
      const payload = { method };
      if (method === "CARD") payload.cardLast4 = fields.cardLast4;
      if (method === "UPI") payload.upiId = fields.upiId;
      if (method === "INSURANCE") payload.policyNumber = fields.policyNumber;
      const updated = await payBill(id, payload);
      setBill(updated);
    } catch (e) {
      setError(e.response?.data?.message || "Payment failed");
    } finally {
      setPaying(false);
    }
  };

  if (!bill) {
    return (
      <AppLayout title="Invoice">
        {error ? <div className="alert alert-error">{error}</div> : <div className="alert-empty">Loading...</div>}
      </AppLayout>
    );
  }

  return (
    <AppLayout
      title="Invoice"
      actions={<button className="btn btn-ghost" onClick={() => navigate("/bills")}>Back to billing</button>}
    >
      {error && <div className="alert alert-error">{error}</div>}

      <div className="invoice">
        <div className="invoice-head">
          <div>
            <h2>{bill.patientName}</h2>
            <div className="invoice-no">{bill.invoiceNumber}</div>
            <span className={`bill-status bill-${bill.status}`} style={{ marginTop: 8, display: "inline-block" }}>
              {bill.status}
            </span>
          </div>
          <div className="invoice-meta">
            <div><strong>Hospital Management System</strong></div>
            {bill.doctorName && <div>Doctor: {bill.doctorName}</div>}
            {bill.department && <div>Dept: {bill.department}</div>}
            {bill.createdAt && <div>Raised: {new Date(bill.createdAt).toLocaleDateString()}</div>}
          </div>
        </div>

        <table className="invoice-table">
          <thead>
            <tr>
              <th>Description</th>
              <th className="num">Qty</th>
              <th className="num">Unit</th>
              <th className="num">Amount</th>
            </tr>
          </thead>
          <tbody>
            {bill.items?.map((it) => (
              <tr key={it.id}>
                <td>{it.description}</td>
                <td className="num">{it.quantity}</td>
                <td className="num">{money(it.unitPrice)}</td>
                <td className="num">{money(it.lineTotal)}</td>
              </tr>
            ))}
          </tbody>
        </table>

        <div className="totals">
          <div className="totals-row"><span>Subtotal</span><span>{money(bill.subtotal)}</span></div>
          <div className="totals-row"><span>Tax</span><span>{money(bill.tax)}</span></div>
          <div className="totals-row grand"><span>Total</span><span>{money(bill.total)}</span></div>
        </div>

        {bill.status === "PAID" && (
          <div className="paid-stamp">
            <strong>Paid via {bill.paymentMethod}</strong> · Ref {bill.paymentReference}
            {Number(bill.insuranceCovered) > 0 && (
              <div>Insurer covered {money(bill.insuranceCovered)} · Collected {money(bill.amountPaid)}</div>
            )}
            {bill.paymentNote && <div>{bill.paymentNote}</div>}
          </div>
        )}
      </div>

      {bill.status === "PENDING" && (
        <div className="panel" style={{ marginTop: 24 }}>
          <h3 className="form-section-title" style={{ marginTop: 0 }}>Take Payment</h3>
          <form onSubmit={handlePay}>
            <div className="method-grid">
              {METHODS.map((m) => (
                <button
                  type="button"
                  key={m}
                  className={`method-btn ${method === m ? "active" : ""}`}
                  onClick={() => setMethod(m)}
                >
                  {m}
                </button>
              ))}
            </div>

            {method === "CARD" && (
              <div className="form-field">
                <label>Card last 4 digits *</label>
                <input
                  value={fields.cardLast4}
                  onChange={(e) => setFields({ ...fields, cardLast4: e.target.value })}
                  maxLength={4}
                  placeholder="4242"
                />
              </div>
            )}
            {method === "UPI" && (
              <div className="form-field">
                <label>UPI ID *</label>
                <input
                  value={fields.upiId}
                  onChange={(e) => setFields({ ...fields, upiId: e.target.value })}
                  placeholder="name@bank"
                />
              </div>
            )}
            {method === "INSURANCE" && (
              <div className="form-field">
                <label>Policy number *</label>
                <input
                  value={fields.policyNumber}
                  onChange={(e) => setFields({ ...fields, policyNumber: e.target.value })}
                  placeholder="POL-123456"
                />
                <small style={{ color: "var(--hms-muted)" }}>
                  The insurer covers a share of the total; the rest is collected as co-pay.
                </small>
              </div>
            )}

            <div className="form-actions">
              <button className="btn btn-primary" type="submit" disabled={paying}>
                {paying ? "Processing..." : `Pay ${money(bill.total)}`}
              </button>
            </div>
          </form>
        </div>
      )}
    </AppLayout>
  );
}

export default InvoicePage;
