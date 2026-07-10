# News Aggregator

A self-hosted news reader that fetches German news, extracts the full articles, and generates short AI summaries in simple German (B1 level).

**Live demo:** https://news-aggregator-1-sw97.onrender.com

I built this to follow German news while learning the language — the summaries are short enough to read quickly and simple enough to understand.

## Features

- Fetches news hourly from the Tagesschau RSS feed
- Extracts full article text with Jsoup
- Generates ~35-word summaries in B1-level German via the Anthropic API
- Stores articles in PostgreSQL, skipping duplicates
- REST API (`/api/news`) serving the latest 140 articles
- Frontend with live search, pagination, and a dark theme
- Runs 24/7 on Render, kept awake by an external cron ping

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4 |
| Database | PostgreSQL (Neon) |
| Scraping | Jsoup |
| AI | Anthropic API (Claude Haiku) |
| Frontend | Vanilla HTML / CSS / JavaScript |
| Deployment | Docker, Render |
| Analytics | GoatCounter |

## How It Works

```
Tagesschau RSS  →  RssFetcher     (parse feed, filter noise)
                →  ArticleFetcher (scrape full text)
                →  AiSummarizer   (Claude → B1 German summary)
                →  NewsDatabase   (PostgreSQL, dedupe by link)
                →  /api/news      (REST endpoint)
                →  index.html     (search, pagination)
```

A `@Scheduled` task refreshes the news every hour. Only articles without a summary are sent to the AI, so re-running an update costs nothing extra.

## Running Locally

**Requirements:** Java 21, Maven, PostgreSQL, an Anthropic API key.

1. Create a database:

   ```sql
   CREATE DATABASE news_aggregator;
   ```

2. Set the environment variables:

   ```
   DATABASE_URL=jdbc:postgresql://localhost:5432/news_aggregator
   DATABASE_USER=postgres
   DATABASE_PASSWORD=your_password
   ANTHROPIC_API_KEY=your_key
   ```

3. Run it:

   ```bash
   mvn spring-boot:run
   ```

4. Open http://localhost:8080

Visit `/update` to trigger a manual refresh.

## Deployment

The app ships as a multi-stage Docker image (Maven build → JRE runtime) and deploys to Render on every push to `master`. The database runs on Neon, and an external cron job pings the service so the free-tier instance stays awake and the scheduler keeps running.

No credentials live in the code — everything is read from environment variables.

## Why I Built It

German news sites are written well above my reading level, so keeping up with the news meant slow, frustrating reading. This app strips each story down to the facts and rewrites it at B1, which is exactly where I am. It also gave me a reason to learn the full stack end to end: scraping, databases, REST APIs, Docker, and cloud deployment.
