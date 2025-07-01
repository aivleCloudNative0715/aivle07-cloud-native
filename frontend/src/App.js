import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

// 페이지 컴포넌트들 import
import MainPage from "@/pages/MainPage";
import { LoginPage, SignUpPage } from "@/pages/AuthPages";
import MyPage from "@/pages/MyPage"; // 마이페이지
import ManuscriptEditor from "@/pages/ManuscriptEditor"; // 원고 작성 페이지
import AllBooksPage from "@/pages/AllBooksPage"; // 전체 책 조회 페이지

function App() {
  // 실제 구현에서는 로그인 여부, 작가 여부를 Context 또는 전역 상태로 관리
  const isLoggedIn = true; // TODO: 실제 로그인 상태로 교체
  const isAuthor = true;   // TODO: 실제 권한 확인 로직으로 교체

  return (
      <Router>
        <Routes>
          <Route path="/" element={<MainPage isLoggedIn={isLoggedIn} />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignUpPage />} />
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/manuscript" element={<ManuscriptEditor />} />
          <Route path="/books" element={<AllBooksPage />} />
          {/* 필요 시 개별 책 상세 페이지도 추가 가능 */}
          {/* <Route path="/book/:id" element={<BookDetail />} /> */}
        </Routes>
      </Router>
  );
}

export default App;