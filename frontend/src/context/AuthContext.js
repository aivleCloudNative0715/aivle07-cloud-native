import React, { createContext, useState, useEffect, useContext } from "react";
import {jwtDecode} from "jwt-decode";

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const API_BASE = process.env.REACT_APP_API_URL;

    useEffect(() => {
        const savedUser = JSON.parse(localStorage.getItem("user"));
        if (savedUser) setUser(savedUser);
    }, []);

    const parseJwt = (token) => {
        try {
            const decoded = jwtDecode(token);
            return {
                email: decoded.email,
                userId: decoded.userId,
                isAuthor: decoded.isAuthor || false,
                isAdmin: decoded.isAdmin || false,
            };
        } catch (e) {
            console.error("Invalid JWT:", e);
            return null;
        }
    };

    // ✅ 회원가입 함수
    const signUp = async ({ email, password, userName, isKt }) => {
        try {
            const response = await fetch(`${API_BASE}/users/signup`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password, isKt, userName }),
            });

            if (!response.ok) {
                const errorText = await response.text();
                return { success: false, errorCode: response.status, message: errorText };
            }

            const data = await response.json();
            return { success: true, data };
        } catch (error) {
            console.error("SignUp error:", error);
            return { success: false, errorCode: 500, message: "서버 오류" };
        }
    };

    // ✅ 로그인 함수
    const login = async (email, password) => {
        try {
            const response = await fetch(`${API_BASE}/users/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password }),
            });

            if (!response.ok) throw new Error("로그인 실패");

            const data = await response.json();
            const token = data.accessToken;

            const decoded = parseJwt(token);
            if (!decoded) return { success: false, data: null };

            const userInfo = {
                token,
                tokenType: data.tokenType,
                email: decoded.email,
                userId: decoded.userId,
                isAuthor: decoded.isAuthor,
                isAdmin: decoded.isAdmin,
                username: data.userName
            };

            setUser(userInfo);
            localStorage.setItem("user", JSON.stringify(userInfo));

            return { success: true, data: userInfo };
        } catch (error) {
            console.error("Login error:", error);
            return { success: false, data: null };
        }
    };

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
        isAdmin: user?.isAdmin || false,
        token: user?.token || null,
        login,
        logout,
        signUp,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);