# Running the agent on Amazon Bedrock

The app calls Claude through Amazon Bedrock. It uses your normal AWS login, so no keys go in this project.

## 1. Create the AWS account and protect it (15 min)

1. Sign up at https://aws.amazon.com. New accounts get **$100 in credits**, plus **$20 for each** starter
   activity you complete (up to $100 more). Credits expire after 6 months.
2. **Set a budget alert first.** Go to Billing and Cost Management > Budgets > Create budget, and create a
   monthly cost budget of **$20** that emails you. This is also one of the starter activities, so it earns $20.
3. Turn on MFA for the root user (IAM > Security recommendations).

## 2. Create a user for the app (5 min)

Don't use the root account for day-to-day work.

1. IAM > Users > Create user, for example `support-agent-dev`.
2. Attach the managed policy **AmazonBedrockLimitedAccess**, or create an inline policy with just
   `bedrock:InvokeModel` and `bedrock:InvokeModelWithResponseStream`.
3. Open the user > Security credentials > Create access key > "Command Line Interface (CLI)". Copy both values.

## 3. Log in on your machine (2 min)

Install the AWS CLI (https://aws.amazon.com/cli/), then run:

```bash
aws configure
# AWS Access Key ID:     <from step 2>
# AWS Secret Access Key: <from step 2>
# Default region name:   us-east-1
# Default output format: json
```

This stores the keys in `~/.aws/credentials` on your computer. **Never paste them into this project or
commit them.**

## 4. Get access to Claude in Bedrock (5 min)

1. Open the Bedrock console in **us-east-1** > Model catalog, and find the Claude model you want.
2. If it asks you to request access or fill in a one-time use-case form for Anthropic models, do that. It is
   usually approved quickly.
3. Copy the model's ID. Many newer models can only be called through a *cross-region inference profile*, whose
   ID has a prefix such as `us.` or `global.` (for example `us.anthropic.claude-...`). Use whatever ID the
   console shows under Inference profiles if a plain model ID fails.

Check that access works from the command line:

```bash
aws bedrock-runtime converse \
  --model-id <model-id-from-console> \
  --messages '[{"role":"user","content":[{"text":"Say hello"}]}]'
```

## 5. Run the app

```bash
docker compose up -d postgres
export LLM_PROVIDER=bedrock
export LLM_MODEL=<model-id-from-console>
export AWS_REGION=us-east-1
mvn spring-boot:run
```

Or run everything in Docker. Compose mounts your `~/.aws` folder read-only into the app container:

```bash
LLM_MODEL=<model-id-from-console> docker compose up --build
```

Then:

```bash
curl -s localhost:8080/api/chat -H 'Content-Type: application/json' \
  -d '{"sessionId":"demo","message":"I get error 1603 when installing on Windows"}'
```

A working setup returns `"escalated": false` with an answer that cites `installation-guide.md`.

## Troubleshooting

| Error in the app log | Fix |
|---|---|
| `Unable to load credentials from any of the providers` | Run `aws configure` (step 3), or set `AWS_PROFILE` if you use a named profile |
| `AccessDeniedException ... not authorized to perform: bedrock:InvokeModel` | Attach the policy from step 2 to your user |
| `You don't have access to the model` | Request model access (step 4) |
| `Invocation of model ID ... with on-demand throughput isn't supported` | Use the inference profile ID (`us.` or `global.` prefix) from the console |
| `ValidationException ... model identifier is invalid` | Check the ID and that `AWS_REGION` matches the region where you enabled the model |

## Cost

You pay per token from your credits. A chat question uses about 3,000 input tokens (the question plus 5
document excerpts) and a few hundred output tokens, across two model calls (classification and answer). Check
current prices on the Bedrock pricing page. Smaller, cheaper Claude models work too: set `LLM_MODEL` to their ID.

## Cleaning up

Bedrock only charges when you call it, so nothing keeps costing money while the app is stopped. If you later
create RDS databases or EC2 servers, delete them when you're done.
