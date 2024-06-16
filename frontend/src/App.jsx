import { BrowserRouter, Routes, Route } from "react-router-dom";

import MainPage from './components/mainPage/MainPage.jsx';

function App() {
  return (
    <div className="App">
      <BrowserRouter>
        <Routes>
          <Route
          path='/'
          element={<MainPage />}
          >
          </Route>
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;