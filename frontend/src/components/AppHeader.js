import React from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Button } from "../components/ui/button";
import BackButton from "../components/ui/backButton";
import { useAuth } from "../context/AuthContext";

export default function AppHeader() {
    const navigate = useNavigate();
    const location = useLocation();
    const { isLoggedIn, isAuthor, isAdmin, logout } = useAuth();

    const showBackButton = location.pathname !== "/";
    const hideManuscriptButton = location.pathname.startsWith("/manuscriptList");
    const hideAuthors = location.pathname.startsWith("/admin");

    const handleLogout = () => {
        logout();
        navigate("/");
    };

    return (
        <header className="flex bg-sky-950 text-white justify-between items-center px-6 py-4 shadow-md border-b">
            <div className="flex items-center gap-4">
                {showBackButton && <BackButton />}
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

                        {isAuthor && !hideManuscriptButton && (
                            <Button onClick={() => navigate("/manuscriptList")}>
                                원고 조회
                            </Button>
                        )}

                        {isAdmin && !hideAuthors && (
                            <Button onClick={() => navigate("/admin/authors")}>
                                작가 조회
                            </Button>
                        )}

                        <Button variant="ghost" onClick={handleLogout}>
                            로그아웃
                        </Button>
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
