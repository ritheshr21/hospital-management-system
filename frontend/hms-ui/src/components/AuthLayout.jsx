function AuthLayout({ title, children }) {
  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>{title}</h1>
        {children}
      </div>
    </div>
  );
}

export default AuthLayout;