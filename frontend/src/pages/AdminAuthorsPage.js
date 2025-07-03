import React, { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { Button } from "../components/ui/button";
import AppHeader from "../components/AppHeader";
import { useNavigate } from "react-router-dom";

export default function AdminAuthorsPage() {
    const { user } = useAuth();
    const API_BASE = process.env.REACT_APP_API_URL;
    const [authors, setAuthors] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // 🔑 필터: APPLIED | ACCEPTED | REJECTED
    const [filter, setFilter] = useState("APPLIED");
    const navigate = useNavigate();

    useEffect(() => {
        const fetchAuthors = async () => {
            try {
                let url;
                if (filter === "ACCEPTED") {
                    url = `${API_BASE}/authors/accepted`;
                } else if (filter === "REJECTED") {
                    url = `${API_BASE}/authors/rejected`;
                } else {
                    // APPLIED
                    url = `${API_BASE}/authors/applications`;
                }

                const res = await fetch(url, {
                    headers: { Authorization: `Bearer ${user.token}` },
                });
                if (!res.ok) throw new Error("작가 목록을 불러올 수 없습니다.");
                const data = await res.json();
                setAuthors(
                    // /authors/applications 가 모든 상태를 주는 경우를 대비해 한 번 더 거르기
                    filter === "APPLIED" ? data.filter((a) => a.status === "APPLIED") : data
                );
            } catch (e) {
                setError(e.message);
            } finally {
                setLoading(false);
            }
        };

        fetchAuthors();
    }, [filter, API_BASE, user]);

    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader />

            <main className="container mx-auto px-6 py-8 max-w-4xl">
                <h2 className="text-2xl font-bold mb-4">👑 작가 관리</h2>

                {/* 필터 버튼: ALL 제거 */}
                <div className="flex gap-2 mb-4">
                    {[
                        { key: "APPLIED", label: "승인 요청" },
                        { key: "ACCEPTED", label: "승인 완료" },
                        { key: "REJECTED", label: "반려" },
                    ].map(({ key, label }) => (
                        <Button
                            key={key}
                            variant={filter === key ? "" : "secondary"}
                            onClick={() => setFilter(key)}
                        >
                            {label}
                        </Button>
                    ))}
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
                            <th className="p-2 border">상태</th>
                        </tr>
                        </thead>
                        <tbody>
                        {authors.map((a) => (
                            <tr
                                key={a.id}
                                onClick={() =>
                                    navigate(`/admin/authors/${a.id}`, {
                                        state: { ...a }, // 상세 페이지로 필요한 데이터 전송
                                    })
                                }
                                className="hover:bg-gray-50 cursor-pointer"
                            >
                                <td className="p-2 border text-center">{a.id}</td>
                                <td className="p-2 border">{a.authorName}</td>
                                <td className="p-2 border">{a.authorId}</td>
                                <td className="p-2 border text-center">
                                    {a.status === "ACCEPTED"
                                        ? "✅ 승인 완료"
                                        : a.status === "REJECTED"
                                            ? "❌ 반려됨"
                                            : "⌛ 승인 요청"}
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
