import React, { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import AppHeader from "../components/AppHeader";
import {Button} from "../components/ui/button";
import {getStatusLabel} from "../lib/statusUtils";

export default function ManuscriptList() {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [manuscripts, setManuscripts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const API_BASE = process.env.REACT_APP_API_URL;

    useEffect(() => {
        if (!user?.isAuthor) return;

        const fetchManuscripts = async () => {
            try {
                const res = await fetch(`${API_BASE}/manuscripts/${user.userId}`, {
                    headers: {
                        Authorization: `${user.tokenType ?? "Bearer"} ${user.token}`,
                    },
                });
                if (!res.ok) throw new Error("원고 목록을 불러올 수 없습니다.");
                const data = await res.json();
                setManuscripts(data);
            } catch (e) {
                setError(e.message);
            } finally {
                setLoading(false);
            }
        };

        fetchManuscripts();
    }, [API_BASE, user]);

    if (!user?.isAuthor) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <p>작가만 접근할 수 있는 페이지입니다.</p>
            </div>
        );
    }

    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader isLoggedIn={!!user} isAuthor={user.isAuthor} />
            <main className="container mx-auto px-6 py-10 max-w-4xl">
                {loading && <p>원고 목록 불러오는 중...</p>}
                {error && <p className="text-red-600">{error}</p>}

                {!loading && !error && manuscripts.length === 0 && (
                    <p className="text-gray-500 text-center">작성한 원고가 없습니다.</p>
                )}

                {!loading && !error && (
                    <>
                        <div className="flex items-center justify-between mb-4">
                          <h2 className="text-2xl font-bold">✍️ 내 원고 목록</h2>
                          <Button
                              onClick={() =>
                             navigate(`/manuscript/${user.userId}/new`)
                            }
                          >
                            원고 등록
                          </Button>
                        </div>

                        <table className="w-full table-auto border">
                            <thead className="bg-gray-100">
                            <tr>
                                <th className="p-2 border">ID</th>
                                <th className="p-2 border">제목</th>
                                <th className="p-2 border">작성일</th>
                                <th className="p-2 border">상태</th>
                            </tr>
                            </thead>
                            <tbody>
                            {manuscripts.map((m) => (
                                <tr
                                    key={m.id}
                                    className="hover:bg-gray-100 cursor-pointer"
                                    onClick={() => navigate(`/manuscript/${m.authorId}/${m.manuscriptId}`)}
                                >
                                    <td className="p-2 border text-center">{m.manuscriptId}</td>
                                    <td className="p-2 border">{m.title}</td>
                                    <td className="p-2 border text-center">
                                        {m.lastModifiedAt?.split("T")[0]}
                                    </td>
                                    <td className="p-2 border text-center">{getStatusLabel(m.status)}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </>
                )}
            </main>
        </div>
    );
}
