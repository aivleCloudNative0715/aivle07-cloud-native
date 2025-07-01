import React from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";

interface HeaderProps {
    isLoggedIn: boolean;
    isAuthor: boolean;
}

export default function AppHeader({ isLoggedIn, isAuthor }: HeaderProps) {
    const navigate = useNavigate();

    return (
        <header className="flex justify-between items-center px-6 py-4 shadow-md border-b">
            <h1
                className="text-xl font-bold cursor-pointer"
                onClick={() => navigate("/")}
            >
                걷다가 서재
            </h1>
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
