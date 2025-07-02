import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "../components/ui/button";
import AppHeader from "../components/AppHeader";

export default function AllBooksPage({ isLoggedIn = false, isAuthor = false }) {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState("");

    const books = [
        { id: 1, title: "책 제목 1" },
        { id: 2, title: "책 제목 2" },
        { id: 3, title: "책 제목 3" },
        { id: 4, title: "책 제목 4" },
        { id: 5, title: "책 제목 5" },
    ];

    const filteredBooks = books.filter((book) =>
        book.title.toLowerCase().includes(searchQuery.toLowerCase())
    );

    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader isLoggedIn={isLoggedIn} isAuthor={isAuthor} />

            <main className="container mx-auto px-6 py-8">
                <h2 className="text-2xl font-bold mb-4">📚 전체 책 목록</h2>

                <div className="flex justify-between items-center mb-6">
                    <input
                        type="text"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        placeholder="책 제목 검색"
                        className="border px-4 py-2 rounded-md w-full max-w-xs"
                    />

                    {isAuthor && (
                        <Button className="ml-4" onClick={() => navigate("/manuscript")}>
                            원고 등록
                        </Button>
                    )}
                </div>

                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
                    {filteredBooks.map((book) => (
                        <div
                            key={book.id}
                            className="bg-white border shadow rounded-md p-4 hover:shadow-md cursor-pointer"
                            onClick={() => navigate(`/book/${book.id}`)}
                        >
                            <div className="w-full h-40 bg-gray-200 mb-2" />
                            <div className="text-sm font-semibold text-center">{book.title}</div>
                        </div>
                    ))}
                </div>
            </main>
        </div>
    );
}
