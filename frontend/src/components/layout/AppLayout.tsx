import { Outlet } from 'react-router-dom';
import Footer from './Footer';
import Header from './Header';

function AppLayout() {
  return (
    <>
      <Header />
      <main style={{ minHeight: 'calc(100vh - 128px)', padding: '24px' }}>
        <Outlet />
      </main>
      <Footer />
    </>
  );
}

export default AppLayout;
