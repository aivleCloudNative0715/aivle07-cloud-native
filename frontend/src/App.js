import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import MainPage from "./pages/MainPage";
import { LoginPage, SignUpPage } from "./pages/AuthPages";
import MyPage from "./pages/MyPage";
import ManuscriptEditor from "./pages/ManuscriptEditor";
import AllBooksPage from "./pages/AllBooksPage";
import BookDetail from "./pages/BookDetail";
import ManuscriptList from "./pages/ManuscriptList";
import AdminAuthorsPage from "./pages/AdminAuthorsPage";
import AdminAuthorDetail from "./pages/AdminAuthorDetail";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<MainPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/signup" element={<SignUpPage />} />
                <Route path="/mypage" element={<MyPage />} />

                <Route path="/manuscriptList" element={<ManuscriptList />} />

                <Route path="/manuscript/:authorId/:manuscriptId" element={<ManuscriptEditor />} />

                <Route path="/books" element={<AllBooksPage />} />
                <Route path="/book/:id" element={<BookDetail />} />

                <Route path="/admin/authors" element={<AdminAuthorsPage />} />
                <Route path="/admin/authors/:id" element={<AdminAuthorDetail />} />
            </Routes>
        </Router>
    );
}

export default App;