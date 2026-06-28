import { Outlet } from 'react-router-dom';
import Header from './Header';
import Footer from './Footer';

function OfficerLayout() {
  return (
    <>
      <Header />
      <main style={{ minHeight: 'calc(100vh - 128px)', padding: '24px' }}>
        {/* Placeholder for Officer-specific navigation sidebars or headers */}
        <Outlet />
      </main>
      <Footer />
    </>
  );
}

export default OfficerLayout;
