import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "../components/ui/button";
import AppHeader from "../components/AppHeader";

export default function MainPage({ isLoggedIn }) {
    const navigate = useNavigate();
    const [bestsellers, setBestsellers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const API_BASE = process.env.REACT_APP_API_URL;

    useEffect(() => {
        const fetchBestsellers = async () => {
            try {
                const res = await fetch(`${API_BASE}/books/bestsellers/topN?n=8`);
                if (!res.ok) throw new Error("베스트셀러 목록을 불러오는 데 실패했습니다.");
                const data = await res.json();
                setBestsellers(data); // 리스트로 바로 반환된다고 가정
            } catch (err) {
                setError(err.message || "알 수 없는 오류");
            } finally {
                setLoading(false);
            }
        };

        fetchBestsellers();
    }, [API_BASE]);

    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader isLoggedIn={isLoggedIn} isAuthor={false} />

            <main className="flex-1 container mx-auto px-6 py-12">
                <section className="mb-12">
                    <h2 className="text-2xl font-bold mb-4">📚 베스트셀러</h2>

                    {loading && <p>로딩 중...</p>}
                    {error && <p className="text-red-500">{error}</p>}
                    {!loading && !error && bestsellers.length === 0 && <p>베스트셀러가 없습니다.</p>}

                    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
                        {bestsellers.map((book) => (
                            <div
                                key={book.id}
                                className="bg-white border shadow rounded-md p-4 hover:shadow-md transition cursor-pointer"
                                onClick={() => navigate(`/book/${book.id}`)}
                            >
                                <div
                                    className="w-full h-40 bg-gray-200 mb-2"
                                    style={{
                                        backgroundImage: `url(${book.coverImageUrl})`,
                                        backgroundSize: "cover",
                                        backgroundPosition: "center",
                                    }}
                                />
                                <div className="text-sm font-semibold text-center">{book.title}</div>
                            </div>
                        ))}
                    </div>

                    <div className="flex justify-center mt-6">
                        <Button onClick={() => navigate("/books")}>전체 책 보기</Button>
                    </div>
                </section>
            </main>
        </div>
    );
}
