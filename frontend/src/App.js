import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import MainPage from "./pages/MainPage";
import { LoginPage, SignUpPage } from "./pages/AuthPages";
import MyPage from "./pages/MyPage";
import ManuscriptEditor from "./pages/ManuscriptEditor";
import AllBooksPage from "./pages/AllBooksPage";
import BookDetail from "./pages/BookDetail";
import ManuscriptList from "./pages/ManuscriptList";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<MainPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/signup" element={<SignUpPage />} />
                <Route path="/mypage" element={<MyPage />} />
                <Route path="/manuscriptList" element={<ManuscriptList />} />
                <Route path="/manuscript/:authorId/new" element={<ManuscriptEditor />} />
                <Route path="/manuscript/:authorId/:manuscriptId" element={<ManuscriptEditor />} />
                <Route path="/books" element={<AllBooksPage />} />
                <Route path="/book/:id" element={<BookDetail />} />
            </Routes>
        </Router>
);
}

export default App;