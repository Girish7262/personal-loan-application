import { Outlet } from 'react-router-dom';
import Header from './Header';
import Footer from './Footer';

function AdminLayout() {
  return (
    <>
      <Header />
      <main style={{ minHeight: 'calc(100vh - 128px)', padding: '24px' }}>
        {/* Placeholder for Admin-specific audit sidebars or headers */}
        <Outlet />
      </main>
      <Footer />
    </>
  );
}

export default AdminLayout;
