import { Outlet } from 'react-router-dom';
import Header from './Header';
import Footer from './Footer';

function CustomerLayout() {
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

export default CustomerLayout;
