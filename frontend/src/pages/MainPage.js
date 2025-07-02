import React from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "../components/ui/button";
import AppHeader from "../components/AppHeader";

export default function MainPage({ isLoggedIn }) {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader isLoggedIn={isLoggedIn} isAuthor={false} />

            <main className="flex-1 container mx-auto px-6 py-12">
                <section className="mb-12">
                    <h2 className="text-2xl font-bold mb-4">📚 베스트셀러</h2>
                    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
                        {/* 예시 베스트셀러 카드 */}
                        {[1, 2, 3, 4, 5].map((id) => (
                            <div
                                key={id}
                                className="bg-white border shadow rounded-md p-4 hover:shadow-md transition cursor-pointer"
                                onClick={() => navigate(`/book/${id}`)}
                            >
                                <div className="w-full h-40 bg-gray-200 mb-2" />
                                <div className="text-sm font-semibold text-center">책 제목 {id}</div>
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
