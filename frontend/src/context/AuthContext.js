import React, { createContext, useState, useEffect, useContext } from "react";

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const API_BASE = process.env.REACT_APP_API_URL;

    useEffect(() => {
        const savedUser = JSON.parse(localStorage.getItem("user"));
        if (savedUser) setUser(savedUser);
    }, []);

    // ✅ 회원가입 함수
    const signUp = async ({ email, password, userName, isKt }) => {
        try {
            const response = await fetch(`${API_BASE}/users/signup`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ email, password, isKt, userName }),
            });

            if (!response.ok) throw new Error("회원가입 실패");

            const data = await response.json();

            return { success: true, data };
        } catch (error) {
            console.error("SignUp error:", error);
            return { success: false, data: null };
        }
    };

    // ✅ 로그인 함수
    const login = async (email, password) => {
        try {
            const response = await fetch(`${API_BASE}/users/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ email, password }),
            });

            if (!response.ok) throw new Error("로그인 실패");

            const data = await response.json();

            console.log(data);

            const userInfo = {
                token: data.accessToken,
                tokenType: data.tokenType,
                userId: data.userId,
                email: data.email,
                isAuthor: data.isAuthor || false,
            };

            setUser(userInfo);
            localStorage.setItem("user", JSON.stringify(userInfo));

            return { success: true, data: userInfo };
        } catch (error) {
            console.error("Login error:", error);
            return { success: false, data: null };
        }
    };

    // ✅ 로그아웃 함수
    const logout = async () => {
        try {
            await fetch(`${API_BASE}/users/logout`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${user?.token || ""}`,
                },
            });
        } catch (error) {
            console.warn("서버 로그아웃 실패 (무시됨):", error);
        } finally {
            setUser(null);
            localStorage.removeItem("user");
        }
    };

    const value = {
        user,
        isLoggedIn: !!user,
        isAuthor: user?.isAuthor || false,
        token: user?.token || null,
        login,
        logout,
        signUp,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
