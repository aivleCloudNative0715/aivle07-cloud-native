import React from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Button } from "../components/ui/button";
import BackButton from "../components/ui/backButton";

export default function AppHeader({ isLoggedIn, isAuthor }) {
    const navigate = useNavigate();
    const location = useLocation();

    const showBackButton = location.pathname !== "/"; // 메인 페이지가 아닌 경우만 표시

    return (
        <header className="flex bg-sky-950 text-white justify-between items-center px-6 py-4 shadow-md border-b">
            <div className="flex items-center gap-4">
                {showBackButton && (
                    <BackButton />
                )}
                <h1
                    className="text-xl font-bold cursor-pointer"
                    onClick={() => navigate("/")}
                >
                    걷다가 서재
                </h1>
            </div>

            <div className="flex items-center gap-4">
                {isLoggedIn ? (
                    <>
                        <Button onClick={() => navigate("/mypage")}>마이페이지</Button>
                        {isAuthor && (
                            <Button onClick={() => navigate("/manuscript")}>원고 등록</Button>
                        )}
                    </>
                ) : (
                    <>
                        <Button variant="ghost" onClick={() => navigate("/login")}>로그인</Button>
                        <Button variant="ghost" onClick={() => navigate("/signup")}>회원가입</Button>
                    </>
                )}
            </div>
        </header>
    );
}
