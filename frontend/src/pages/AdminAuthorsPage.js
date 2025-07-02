import React, { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { Button } from "../components/ui/button";
import AppHeader from "../components/AppHeader";
import {useNavigate} from "react-router-dom";

export default function AdminAuthorsPage() {
    const { user } = useAuth();               // 관리자 토큰
    const API_BASE = process.env.REACT_APP_API_URL;
    const [authors, setAuthors] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [filter, setFilter] = useState("ALL"); // ALL | APPROVED | REQUESTED
    const navigate = useNavigate();

    // 필터링된 목록
    const filtered = authors.filter((a) =>
        filter === "ALL"
            ? true
            : filter === "APPROVED"
                ? a.approved
                : !a.approved
    );

    useEffect(() => {
        const dummyAuthors = [
            {
                id: 1,
                email: "writer1@example.com",
                name: "김작가",
                approved: true,
            },
            {
                id: 2,
                email: "writer2@example.com",
                name: "이소설",
                approved: false,
            },
            {
                id: 3,
                email: "writer3@example.com",
                name: "박문장",
                approved: true,
            },
            {
                id: 4,
                email: "writer4@example.com",
                name: "최문학",
                approved: false,
            },
        ];

        // 더미 데이터로 바로 세팅
        setAuthors(dummyAuthors);
        setLoading(false);
    }, []);


    // useEffect(() => {
    //     const fetchAuthors = async () => {
    //         try {
    //             const res = await fetch(`${API_BASE}/admin/authors`, {
    //                 headers: {
    //                     Authorization: `${user.tokenType ?? "Bearer"} ${user.token}`,
    //                 },
    //             });
    //             if (!res.ok) throw new Error("작가 목록을 불러올 수 없습니다.");
    //             const data = await res.json(); // [{id,email,name,approved}, ...]
    //             setAuthors(data);
    //         } catch (e) {
    //             setError(e.message);
    //         } finally {
    //             setLoading(false);
    //         }
    //     };
    //     fetchAuthors();
    // }, [API_BASE, user]);

    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader />
            <main className="container mx-auto px-6 py-8 max-w-4xl">
                <h2 className="text-2xl font-bold mb-4">👑 작가 관리</h2>

                {/* 필터 버튼 */}
                <div className="flex gap-2 mb-4">
                    <Button variant={filter === "ALL" ? "" : "secondary"} onClick={() => setFilter("ALL")}>
                        전체
                    </Button>
                    <Button
                        variant={filter === "REQUESTED" ? "" : "secondary"}
                        onClick={() => setFilter("REQUESTED")}
                    >
                        승인 요청
                    </Button>
                    <Button
                        variant={filter === "APPROVED" ? "" : "secondary"}
                        onClick={() => setFilter("APPROVED")}
                    >
                        승인 완료
                    </Button>
                </div>

                {loading && <p>로딩 중...</p>}
                {error && <p className="text-red-600">{error}</p>}

                {!loading && !error && (
                    <table className="w-full table-auto border">
                        <thead className="bg-gray-100">
                        <tr>
                            <th className="p-2 border">ID</th>
                            <th className="p-2 border">이름</th>
                            <th className="p-2 border">이메일</th>
                            <th className="p-2 border">승인 여부</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filtered.map((a) => (
                            <tr className="hover:bg-gray-50 cursor-pointer"
                                onClick={() => navigate(`/admin/authors/${a.id}`)}
                            >
                                <td className="p-2 border text-center">{a.id}</td>
                                <td className="p-2 border">{a.name}</td>
                                <td className="p-2 border">{a.email}</td>
                                <td className="p-2 border text-center">
                                    {a.approved ? "✅ 승인 완료" : "⌛ 승인 요청"}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </main>
        </div>
    );
}
