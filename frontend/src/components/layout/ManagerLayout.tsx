import { Outlet } from 'react-router-dom';
import Header from './Header';
import Footer from './Footer';

function ManagerLayout() {
  return (
    <>
      <Header />
      <main style={{ minHeight: 'calc(100vh - 128px)', padding: '24px' }}>
        {/* Placeholder for Manager-specific navigation sidebars or headers */}
        <Outlet />
      </main>
      <Footer />
    </>
  );
}

export default ManagerLayout;
